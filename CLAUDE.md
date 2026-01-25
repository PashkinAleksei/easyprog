# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**EasyProg** is a Kotlin Multiplatform Mobile (KMP) educational programming game that teaches basic programming concepts through interactive drag-and-drop command programming. The app uses visual variable manipulation where users create command sequences to achieve specific outcomes.

## Architecture

### Module Structure

- **`androidApp/`** - Android-specific implementation using Jetpack Compose
- **`shared/`** - Kotlin Multiplatform common code (currently minimal, most logic in Android)
- **`iosApp/`** - iOS SwiftUI stub (framework consumer only, not fully implemented)

### Key Technologies

- **Kotlin 2.2.21** with Multiplatform
- **Jetpack Compose 1.9.3** for declarative UI
- **Room 2.8.4** with KSP for local database
- **Navigation Compose 2.8.5** with type-safe routing via Kotlin Serialization
- **Kotlinx Immutable Collections** for functional state management

### Core Architecture Patterns

1. **MVVM with Listener Pattern** - ViewModels manage UI state via `StateFlow`, UI interacts via `GameListener` interface
2. **Repository Pattern** - `GameRepository` abstracts database operations
3. **Immutable State** - All state uses `ImmutableList` from kotlinx.collections
4. **Sealed Classes** - Type-safe domain models (`CodePeace`, `CommandItem`, `VictoryCondition`)
5. **Functional Updates** - Commands are functions: `(ImmutableList<CodePeace>) -> ImmutableList<CodePeace>`
6. **Separation of Concerns** - ViewModels are never passed directly to Composables, only via `GameListener` interface

## Common Development Commands

### Building

```bash
# Build entire project
./gradlew build

# Build Android app only
./gradlew :androidApp:build

# Build shared module
./gradlew :shared:build

# Build iOS framework
./gradlew :shared:linkDebugFrameworkIosArm64
```

### Running

```bash
# Install debug APK on connected device
./gradlew :androidApp:installDebug

# Run Android app
./gradlew :androidApp:assembleDebug && adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

### Testing

```bash
# Run all tests
./gradlew test

# Run Android unit tests
./gradlew :androidApp:testDebugUnitTest

# Run shared module tests
./gradlew :shared:testDebugUnitTest
```

### Code Generation & Cleanup

```bash
# Generate Room database code (KSP)
./gradlew :androidApp:kspDebugKotlin

# Clean build artifacts
./gradlew clean
```

## Code Architecture Details

### Database Layer

**Location:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/data/`

Two Room entities:

1. **`LevelProgressEntity`** - Tracks completed levels for unlocking progression
2. **`SavedCommandEntity`** - Persists user's command sequences per level

**Important:** Database is a singleton accessed via `EasyProgApplication.getInstance().database`. All operations should go through `GameRepository`, which handles entity ↔ domain model conversion.

### Game Domain Models

**Location:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/domain/`

- **`CodePeace`** - Sealed class representing game variables (currently only `IntVariable`)
  - Contains nullable `Int?` value, mutability flag, and color index

- **`CommandItem`** - Sealed interface for executable commands
  - `CopyValueCommand` - Copies value from source to target
  - `MoveValueCommand` - Moves value (source becomes null)
  - `IncValueCommand` - Increments variable value by 1
  - `GotoCommand` - Jump control command (creates pairs: label and jump target)
  - Each implements `execute()` to transform `ImmutableList<CodePeace>` and return next command index

### Level System

**Location:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/levels/LevelRepository.kt`

10 hardcoded levels with:
- Initial variables configuration (values, labels, colors)
- Available source commands
- Victory conditions via `VictoryCondition` sealed interface
- Descriptions in Russian

**Adding New Levels:**
1. Add entry to `levels` list in `LevelRepository`
2. Increment `id` sequentially
3. Define `initialCodeItems` with variables
4. Set `availableSourceItems` (Copy and/or Move commands)
5. Create `victoryCondition` using combinators (`All`, `VariableEquals`, etc.)

### Victory Condition System

**Location:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/domain/VictoryCondition.kt`

Flexible validation via sealed interface:
- `VariableEquals(index, expectedValue)` - Exact value check
- `VariableIsNull(index)` - Empty variable
- `VariableSortedAscending(indices)` - Sorted array
- `All(vararg conditions)` - AND combinator for multiple checks

Validated in `GameViewModel.checkVictoryCondition()` after command execution.

### UI State Management

**`GameViewState`** contains:
- `codeItems` - Current variable states (immutable list)
- `commandItems` - User's command sequence
- `sourceItems` - Available commands to drag
- `executingCommandIndex` - Highlights command during animated execution
- `draggedCommandItem` - Currently dragged command during drag-and-drop
- `isCommandColumnHovered` - Hover state for command column
- `itemIndexHovered` - Index of hovered item for insertion preview
- Dialog visibility flags
- `executionSpeed` - Current execution speed setting

**GameListener Interface:**

All UI interactions go through the `GameListener` interface, which `GameViewModel` implements:

```kotlin
interface GameListener {
    // Command management
    fun onAddCommand(command: CommandItem, isNewItem: Boolean): Boolean
    fun onRemoveCommand(index: Int): CommandItem?
    fun onUpdateCommand(index: Int, command: CommandItem)

    // Drag and drop events
    fun onSetDraggedCommandItem(item: CommandItem?)
    fun onColumnItemDrop(event: DragAndDropEvent): Boolean
    fun onTopItemDrop(index: Int, event: DragAndDropEvent): Boolean

    // Execution control
    fun onExecuteCommands()
    fun onAbortExecution()
    fun onCycleExecutionSpeed()

    // UI state
    fun onSetItemIndexHovered(index: Int?)
    fun onShowLevelInfoDialog()
    // ... other methods
}
```

**State Flow:**
```
User drags command → listener.onAddCommand() →
GameViewModel updates state (immutable copy) →
StateFlow emits new state → Compose recomposes →
UI reflects new command list
```

**Important:** ViewModels are NEVER passed directly to Composable functions. Instead:
- Pass `viewState: GameViewState` for data
- Pass `listener: GameListener` for callbacks
- ViewModel implements `GameListener` interface

### Navigation

Type-safe routing via Kotlin Serialization:

```kotlin
sealed class Screens {
    data object LevelMenu : Screens()
    data class Game(val levelId: Int) : Screens()
}
```

Navigate with: `navController.navigate(Screens.Game(levelId = 5))`

## Important Implementation Notes

### Immutable Collections Pattern

All game state uses `kotlinx.collections.immutable.ImmutableList`. Never use mutable lists for state:

```kotlin
// Correct
val newItems = state.commandItems.add(newCommand)
setState(state.copy(commandItems = newItems))

// Incorrect
state.commandItems.add(newCommand) // Compile error, no add() mutation method
```

### Command Execution Flow

Commands execute sequentially with configurable delay for visual feedback:

1. User clicks play button → `listener.onExecuteCommands()` → `GameViewModel.onExecuteCommands()`
2. Execution job starts and stores reference in `executionJob: Job?`
3. Loop through `commandItems` with dynamic `delay(executionSpeed.delayMs)`
4. Set `executingCommandIndex` to highlight current command
5. Execute command: `commandResult = command.execute(codeItems, commandItems, currentCommandIndex)`
6. Update state with `commandResult.newCodeItems`
7. Move to `commandResult.nextCommandIndex` (enables jump control via GoTo)
8. **Check victory condition after EACH command** - if condition met, stop execution immediately
9. If victory achieved, show VictoryDialog and return early
10. If all commands executed without victory, show TryAgainDialog
11. Clear `executionJob` reference

**Victory Check After Each Command:**
- Victory condition is checked after each command execution, not just at the end
- If condition is met mid-execution, execution stops immediately
- Current state is preserved (not reset)
- VictoryDialog is shown
- Level is marked as completed

**Execution can be stopped mid-run:**
- User clicks stop button → `listener.onAbortExecution()` → `GameViewModel.onAbortExecution()`
- Cancels the coroutine job
- Resets `executingCommandIndex` to null
- Restores initial code items state

**Execution Speeds:**
- Defined in `ExecutionSpeed` enum (GameViewState.kt)
- `SPEED_05X` - 800ms delay (slow)
- `SPEED_1X` - 400ms delay (normal, default)
- `SPEED_2X` - 200ms delay (fast)
- User cycles through speeds via button during execution
- Speed button replaces clear button when executing

### GoTo Command System

**Location:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/commands/GotoCommand.kt`

GoTo commands enable jump control flow by creating label-target pairs:
- Each pair consists of two commands: `FIRST` (label/marker) and `SECOND` (jump target)
- Commands are linked via `pairId` (unique timestamp from `Calendar.getInstance().timeInMillis`)
- Each pair has a `colorIndex` for visual distinction
- Maximum pairs per level defined in `Config.MAX_GOTO_COMMANDS`

**Color Index Management:**

When adding new GoTo pairs, color indices must be managed to prevent duplication:

```kotlin
private fun findFirstAvailableColorIndex(commands: ImmutableList<CommandItem>): Int {
    val usedColorIndices = mutableSetOf<Int>()
    commands.forEach { command ->
        if (command is GotoCommand) {
            usedColorIndices.add(command.colorIndex)
        }
    }
    // Find first unused index from 0 to MAX_GOTO_COMMANDS-1
    for (i in 0 until Config.MAX_GOTO_COMMANDS) {
        if (i !in usedColorIndices) {
            return i
        }
    }
    return 0
}
```

**Adding GoTo Pairs:**
- When dragging a new GoTo command from source, automatically create both label and target
- Assign next available color index via `findFirstAvailableColorIndex()`
- Generate unique `pairId` for linking
- Insert both commands into command list
- Scroll to show the newly added target command

**Removing GoTo Pairs:**
- `removeCommandPair()` deletes both commands in the pair by matching `pairId`
- Ensures no orphaned labels or targets remain

### CommandRow Architecture

**Location:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/ui/components/`

CommandRow UI components are split by command type for maintainability:

1. **TwoVariableCommandRow.kt** - For `CopyValueCommand` and `MoveValueCommand`
   - Shows source and target variable dropdowns
   - Handles drag-and-drop for variable selection

2. **SingleVariableCommandRow.kt** - For `IncValueCommand`
   - Shows single target variable dropdown
   - Simpler UI with only one variable selector

3. **GotoCommandRow.kt** - For `GotoCommand`
   - Shows label icon or jump icon based on pair type
   - Displays color-coded visual indicator
   - No variable selection needed

**Common Modifier Pattern:**

All CommandRow composables accept a shared `modifier` parameter generated by `commandRowModifier()`:

```kotlin
// In CommandRowModifier.kt
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.commandRowModifier(
    index: Int,
    isThisCommandExecuting: Boolean,
    isCommandExecution: Boolean,
    text: String,
    viewModel: GameViewModel,
): Modifier {
    val backgroundColor = if (isThisCommandExecuting)
        AppColors.CommandBackgroundExecuting
    else
        AppColors.MAIN_COLOR

    val dragModifier = if (!isCommandExecution) {
        this.dragAndDropSource { _ ->
            viewModel.setDraggedCommandItem(viewModel.removeCommand(index))
            DragAndDropTransferData(ClipData.newPlainText("dragged_item", text))
        }
    } else {
        this  // No drag during execution
    }

    return dragModifier
        .fillMaxWidth()
        .padding(horizontal = dp16)
        .background(color = backgroundColor, shape = AppShapes.CORNER_MEDIUM)
        .padding(dp12)
}
```

**Usage in CommandsColumn:**
```kotlin
val commandRowModifier = Modifier.commandRowModifier(
    index = index,
    isThisCommandExecuting = viewState.executingCommandIndex == index,
    isCommandExecution = isCommandExecution,
    text = commandText,
    viewModel = viewModel
)

when (item) {
    is TwoVariableCommand -> item.CommandRow(commandRowModifier, index, codeItems, viewModel)
    is SingleVariableCommand -> item.CommandRow(commandRowModifier, index, codeItems, viewModel)
    is GotoCommand -> item.CommandRow(commandRowModifier)
}
```

### Drag and Drop Blocking During Execution

**Implementation:** `GameViewState.isCommandExecution` computed property

```kotlin
val isCommandExecution: Boolean
    get() = executingCommandIndex != null
```

**Blocking Points:**

1. **Source Column** (SourceColumn.kt)
   - Conditionally applies `dragAndDropSource` only when `!isCommandExecution`

2. **CommandRow Modifier** (CommandRowModifier.kt)
   - Returns plain modifier without drag source when executing

3. **Drop Targets** (All drag-and-drop targets)
   - `ColumnDragAndDropTarget.kt` - Returns `false` in `onDrop` during execution
   - `TopItemDragAndDropTarget.kt` - Returns `false` during execution
   - `BotItemDragAndDropTarget.kt` - Returns `false` during execution
   - `GlobalDragAndDropTarget.kt` - Checks `viewState.isCommandExecution` before processing

**Result:** Users cannot drag/drop commands while execution is in progress, preventing mid-execution modifications.

### Clear Commands Functionality

**Location:** GameViewModel.kt, GameDialogs.kt, GameView.kt

Users can clear all commands via delete button (left of play button):

**Button Behavior:**
- Shows trash icon (`ic_delete`) left of play/stop button
- Enabled only when `commandItems.isNotEmpty()`
- Disabled state shows faded icon (alpha 0.3)
- During execution, button is replaced by speed control

**Confirmation Dialog:**
```kotlin
@Composable
fun ClearCommandsDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    GameDialog(
        title = stringResource(R.string.clear_commands_title),  // "Очистить поле команд?"
        message = stringResource(R.string.clear_commands_message),  // "Все добавленные команды будут удалены"
        dismissable = true,
        onDismissRequest = onCancel,
    ) {
        DialogActionButton(iconRes = R.drawable.ic_check, onClick = onConfirm)  // "Да"
        DialogActionButton(iconRes = R.drawable.ic_close, onClick = onCancel)  // "Нет"
    }
}
```

**Clear Operation:**
```kotlin
fun clearCommands() {
    _viewState.update {
        it.copy(commandItems = persistentListOf())
    }
    saveCommandsToDb()  // Persist empty list to database
}
```

**Dialog State Management:**
- `showClearCommandsDialog: Boolean` in GameViewState
- `showClearCommandsDialog()` / `hideClearCommandsDialog()` in GameViewModel

### Database Transactions

`SavedCommandDao.replaceCommandsForLevel()` uses `@Transaction` for atomic updates:
1. Delete all commands for level
2. Insert new command list

Always use this method rather than separate delete + insert calls.

### Color Palette

Variables use a predefined 11-color palette in `AppColors.variableColors`:
- Indices 0-10 map to distinct colors (reds, greens, blues, purples, oranges, teals)
- Assigned via `colorIndex` in `IntVariable`

### String Resources

All UI strings are in Russian in `res/values/strings.xml`. Command names:
- "Копировать" (Copy) - `R.string.copy_value`
- "Переместить" (Move) - `R.string.move_value`

## Build Configuration

### Version Catalog

Dependencies managed in `gradle/libs.versions.toml`:
- Update versions there, not in build.gradle.kts files
- Access via `libs.` prefix in build scripts

### KSP Code Generation

Room database code generation runs automatically during build via KSP plugin. If DAO changes don't reflect:

```bash
./gradlew clean :androidApp:kspDebugKotlin
```

### Target Platforms

- **Android:** Min SDK 24 (Android 7.0), Target SDK 36 (Android 15)
- **iOS:** x64, Arm64, Simulator Arm64 (framework only, no full app implementation yet)

## Composable Preview Best Practices

### Never Pass ViewModel to Composables

**Bad:**
```kotlin
@Composable
fun GameView(viewModel: GameViewModel) { // ❌ Never do this
    val viewState by viewModel.viewState.collectAsState()
    // ...
}
```

**Good:**
```kotlin
@Composable
fun GameView(
    viewState: GameViewState,
    listener: GameListener,
) { // ✅ Pass state and listener interface
    // ...
}
```

### Preview Setup

**Location:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/preview/`

Use fake implementations for Preview to avoid ViewModel side effects (DB access, network, I/O):

```kotlin
@Preview
@Composable
private fun GameViewPreview() {
    MyApplicationTheme {
        GameView(
            viewState = PreviewGameState.getLevel1(), // Predefined state
            listener = PreviewGameListener(), // Fake implementation
            onBackToMenu = {}
        )
    }
}
```

**PreviewGameListener** - Fake implementation of `GameListener` with no-op methods
**PreviewGameState** - Factory object with predefined `GameViewState` instances for testing different scenarios

### Why This Matters

- Preview executes in IDE, not in test/runtime environment
- ViewModels trigger database operations that fail in Preview
- Mocking frameworks (Mockito, MockK) don't work in Preview
- Fake implementations prevent "ViewModel operations not supported" warnings

## Common Gotchas

1. **Never Pass ViewModel to Composables:** Always use `viewState: GameViewState` and `listener: GameListener` instead
2. **Application Singleton:** Database and repository accessed via `EasyProgApplication.getInstance()`, requires application context
3. **Landscape Only:** App forces landscape orientation in AndroidManifest
4. **No iOS Implementation:** iOS app is just a stub that displays greeting from shared module
5. **Immutable State:** Forgetting to use immutable collections breaks state management
6. **Room KSP:** After entity/DAO changes, rebuild with KSP or IDE won't recognize generated code
7. **Victory Conditions:** Indices in `VictoryCondition` are 0-based, matching `codeItems` list positions
8. **GoTo Color Indices:** Always use `findFirstAvailableColorIndex()` when creating new pairs to prevent color duplication after deleting/re-adding
9. **Execution Job Management:** Must store `executionJob` reference to enable stop functionality; always null-check before canceling
10. **CommandRow Refactoring:** When modifying command UI, remember components are split across three files (TwoVariable, SingleVariable, Goto)
11. **Drag During Execution:** All drag sources and drop targets must check `isCommandExecution` to block interactions during command execution
12. **Execution Speed State:** ExecutionSpeed is part of GameViewState and persists across replays within same level session
13. **Preview Fakes:** Always use `PreviewGameListener` and `PreviewGameState` in Composable Previews, never real ViewModels
14. **Victory Check Timing:** Victory condition is checked after EACH command execution, not just at the end of sequence

## Code Locations Quick Reference

- **ViewModels:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/`
  - `GameViewModel.kt` - Main game state and command management
  - `MenuViewModel.kt` - Level menu state

- **Database:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/data/`
  - `GameRepository.kt` - Database operations facade
  - `SavedCommandEntity.kt` / `LevelProgressEntity.kt` - Room entities
  - `SavedCommandDao.kt` / `LevelProgressDao.kt` - Room DAOs

- **Commands:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/commands/`
  - `CommandItem.kt` - Sealed interface for all commands
  - `CopyValueCommand.kt` / `MoveValueCommand.kt` / `IncValueCommand.kt`
  - `GotoCommand.kt` - Jump control with pair system

- **UI Components:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/ui/components/`
  - `TwoVariableCommandRow.kt` - UI for Copy/Move commands
  - `SingleVariableCommandRow.kt` - UI for Inc command
  - `GotoCommandRow.kt` - UI for GoTo command pairs
  - `CommandRowModifier.kt` - Common modifier extension
  - `VariableBox.kt` - Variable display component

- **UI Columns:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/ui/columns/`
  - `CodeColumn.kt` - Left column with variables
  - `CommandsColumn.kt` - Center column with user's command sequence
  - `SourceColumn.kt` - Right column with available commands

- **Drag & Drop:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/drag_and_drop_target/`
  - `ColumnDragAndDropTarget.kt` - Drop on empty column
  - `TopItemDragAndDropTarget.kt` / `BotItemDragAndDropTarget.kt` - Insert between commands
  - `GlobalDragAndDropTarget.kt` - Background drop handler

- **Dialogs:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/ui/dialogs/`
  - `GameDialogs.kt` - Victory, TryAgain, LevelInfo, ClearCommands dialogs

- **Screens:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/ui/screens/`
  - `GameView.kt` - Main game screen
  - `MenuView.kt` - Level selection screen

- **Levels:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/levels/`
  - `LevelRepository.kt` - All level definitions
  - `LevelConfig.kt` - Level configuration data class

- **Domain Models:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/`
  - `CodePeace.kt` - Variable representation
  - `VictoryCondition.kt` - Victory validation logic
  - `GameViewState.kt` - UI state with ExecutionSpeed enum

- **Config:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/`
  - `Config.kt` - Global configuration (MAX_GOTO_COMMANDS, etc.)

- **Navigation:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/navigation/`
  - `Screens.kt` - Type-safe routing definitions

- **Theme:** `androidApp/src/main/java/ru/lemonapes/easyprog/android/ui/theme/`
  - `AppColors.kt` - Color palette including variable colors
  - `AppDimensions.kt` - Spacing and sizes
  - `AppShapes.kt` - Corner radius definitions

## Project Namespace

Android app: `ru.lemonapes.easyprog.android`
Shared module: `ru.lemonapes.easyprog`
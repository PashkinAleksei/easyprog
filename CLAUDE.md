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

1. **MVVM** - ViewModels manage UI state via `StateFlow`
2. **Repository Pattern** - `GameRepository` abstracts database operations
3. **Immutable State** - All state uses `ImmutableList` from kotlinx.collections
4. **Sealed Classes** - Type-safe domain models (`CodePeace`, `CommandItem`, `VictoryCondition`)
5. **Functional Updates** - Commands are functions: `(ImmutableList<CodePeace>) -> ImmutableList<CodePeace>`

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
  - Each implements `invoke()` to transform `ImmutableList<CodePeace>`

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

**`MainViewState`** contains:
- `codeItems` - Current variable states (immutable list)
- `commandItems` - User's command sequence
- `sourceItems` - Available commands to drag
- `executingCommandIndex` - Highlights command during animated execution
- Dialog visibility flags

**State Flow:**
```
User drags command → GameViewModel.onAddCommand() →
Update state (immutable copy) → Compose recomposes →
UI reflects new command list
```

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

Commands execute sequentially with 500ms delay for visual feedback:

1. `GameViewModel.onExecuteCommands()`
2. Loop through `commandItems` with `delay(500)`
3. Set `executingCommandIndex` to highlight current command
4. Invoke command: `newCodeItems = command(currentCodeItems)`
5. Update state with transformed variables
6. Check victory condition after all commands complete

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

## Common Gotchas

1. **Application Singleton:** Database and repository accessed via `EasyProgApplication.getInstance()`, requires application context
2. **Landscape Only:** App forces landscape orientation in AndroidManifest
3. **No iOS Implementation:** iOS app is just a stub that displays greeting from shared module
4. **Immutable State:** Forgetting to use immutable collections breaks state management
5. **Room KSP:** After entity/DAO changes, rebuild with KSP or IDE won't recognize generated code
6. **Victory Conditions:** Indices in `VictoryCondition` are 0-based, matching `codeItems` list positions

## Code Locations Quick Reference

- ViewModels: `androidApp/src/main/java/ru/lemonapes/easyprog/android/viewmodels/`
- Database: `androidApp/src/main/java/ru/lemonapes/easyprog/android/data/`
- Domain Models: `androidApp/src/main/java/ru/lemonapes/easyprog/android/domain/`
- UI Components: `androidApp/src/main/java/ru/lemonapes/easyprog/android/ui/`
- Levels: `androidApp/src/main/java/ru/lemonapes/easyprog/android/levels/LevelRepository.kt`
- Navigation: `androidApp/src/main/java/ru/lemonapes/easyprog/android/navigation/`
- Theme: `androidApp/src/main/java/ru/lemonapes/easyprog/android/ui/theme/`

## Project Namespace

Android app: `ru.lemonapes.easyprog.android`
Shared module: `ru.lemonapes.easyprog`
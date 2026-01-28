package ru.lemonapes.easyprog.android.commands

import android.icu.util.Calendar
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.R

/**
 * Команда условного перехода.
 * Проверяет значение переменной: если оно равно 0, переходит к парной команде (SECOND).
 * Если значение не равно 0 или null, продолжает выполнение следующей команды.
 */
@Immutable
data class JumpIfZeroCommand(
    override val id: Long = Calendar.getInstance().timeInMillis,
    val type: PairCommand.PairType,
    override val pairId: Long,
    override val colorIndex: Int,
    override val target: Int? = null, // Переменная для проверки (только для FIRST)
) : PairCommand, SingleVariableCommand {

    companion object {
        val INITIAL = JumpIfZeroCommand(
            type = PairCommand.PairType.FIRST,
            pairId = 0,
            colorIndex = 0,
            target = null
        )
    }

    @StringRes
    override val textRes: Int = when (type) {
        PairCommand.PairType.FIRST -> R.string.command_jump_if_zero_start
        PairCommand.PairType.SECOND -> R.string.command_jump_if_zero_target
    }

    @DrawableRes
    override val iconRes: Int = R.drawable.ic_jump_if_zero
    override val stateId = toString()

    override fun mkCopy() = copy(id = Calendar.getInstance().timeInMillis)

    override fun execute(
        codeItems: ImmutableList<CodePeace>,
        commandItems: ImmutableList<CommandItem>,
        currentCommandIndex: Int,
    ): CommandResult {
        // Найти парную команду
        val pairIndex = commandItems.indexOfFirst {
            it is JumpIfZeroCommand && it.id != id && it.pairId == pairId
        }

        val nextCommandIndex = if (type == PairCommand.PairType.FIRST) {
            // Проверяем условие: если переменная == 0, прыгаем
            val shouldJump = target?.let { targetIndex ->
                val variable = codeItems.getOrNull(targetIndex) as? CodePeace.IntVariable
                variable?.value == 0
            } ?: false

            if (shouldJump && pairIndex != -1) {
                pairIndex // Переход к SECOND
            } else {
                currentCommandIndex + 1 // Продолжение
            }
        } else {
            // SECOND всегда продолжает выполнение
            currentCommandIndex + 1
        }

        return CommandResult(
            newCodeItems = codeItems,
            nextCommandIndex = nextCommandIndex,
        )
    }

    override fun validate(): Boolean {
        // FIRST команда требует выбранную переменную, SECOND всегда валидна
        return when (type) {
            PairCommand.PairType.FIRST -> target != null
            PairCommand.PairType.SECOND -> true
        }
    }
}
package ru.lemonapes.easyprog.android.levels

import kotlinx.collections.immutable.ImmutableList
import ru.lemonapes.easyprog.android.CodePeace

/**
 * Условие победы для уровня.
 * Позволяет гибко проверять различные состояния codeItems.
 */
sealed interface VictoryCondition {
    fun check(codeItems: ImmutableList<CodePeace>): Boolean

    /**
     * Проверяет значение переменной по индексу.
     */
    data class VariableEquals(
        val index: Int,
        val expectedValue: Int?,
    ) : VictoryCondition {
        override fun check(codeItems: ImmutableList<CodePeace>): Boolean {
            val item = codeItems.getOrNull(index) as? CodePeace.IntVariable ?: return false
            return item.value == expectedValue
        }
    }

    /**
     * Проверяет, что значение переменной не null.
     */
    data class VariableNotNull(
        val index: Int,
    ) : VictoryCondition {
        override fun check(codeItems: ImmutableList<CodePeace>): Boolean {
            val item = codeItems.getOrNull(index) as? CodePeace.IntVariable ?: return false
            return item.value != null
        }
    }

    /**
     * Проверяет, что значение переменной null (пустая).
     */
    data class VariableIsNull(
        val index: Int,
    ) : VictoryCondition {
        override fun check(codeItems: ImmutableList<CodePeace>): Boolean {
            val item = codeItems.getOrNull(index) as? CodePeace.IntVariable ?: return false
            return item.value == null
        }
    }

    /**
     * Проверяет, что значение одной переменной больше другой.
     */
    data class VariableGreaterThan(
        val index: Int,
        val otherIndex: Int,
    ) : VictoryCondition {
        override fun check(codeItems: ImmutableList<CodePeace>): Boolean {
            val item = codeItems.getOrNull(index) as? CodePeace.IntVariable ?: return false
            val other = codeItems.getOrNull(otherIndex) as? CodePeace.IntVariable ?: return false
            val itemValue = item.value ?: return false
            val otherValue = other.value ?: return false
            return itemValue > otherValue
        }
    }

    /**
     * Проверяет, что значение переменной больше заданного числа.
     */
    data class VariableGreaterThanValue(
        val index: Int,
        val value: Int,
    ) : VictoryCondition {
        override fun check(codeItems: ImmutableList<CodePeace>): Boolean {
            val item = codeItems.getOrNull(index) as? CodePeace.IntVariable ?: return false
            val itemValue = item.value ?: return false
            return itemValue > value
        }
    }

    /**
     * Проверяет, что все условия выполнены (AND).
     */
    data class All(
        val conditions: List<VictoryCondition>,
    ) : VictoryCondition {
        constructor(vararg conditions: VictoryCondition) : this(conditions.toList())

        override fun check(codeItems: ImmutableList<CodePeace>): Boolean {
            return conditions.all { it.check(codeItems) }
        }
    }

    /**
     * Проверяет, что две переменные имеют одинаковое значение.
     */
    data class VariablesEqual(
        val index1: Int,
        val index2: Int,
    ) : VictoryCondition {
        override fun check(codeItems: ImmutableList<CodePeace>): Boolean {
            val item1 = codeItems.getOrNull(index1) as? CodePeace.IntVariable ?: return false
            val item2 = codeItems.getOrNull(index2) as? CodePeace.IntVariable ?: return false
            return item1.value == item2.value && item1.value != null
        }
    }

    /**
     * Проверяет, что переменные отсортированы по возрастанию.
     */
    data class VariablesSortedAscending(
        val indices: List<Int>,
    ) : VictoryCondition {
        constructor(vararg indices: Int) : this(indices.toList())

        override fun check(codeItems: ImmutableList<CodePeace>): Boolean {
            val values = indices.mapNotNull { index ->
                (codeItems.getOrNull(index) as? CodePeace.IntVariable)?.value
            }
            if (values.size != indices.size) return false
            return values == values.sorted()
        }
    }

    /**
     * Проверяет, что переменные отсортированы по убыванию.
     */
    data class VariablesSortedDescending(
        val indices: List<Int>,
    ) : VictoryCondition {
        constructor(vararg indices: Int) : this(indices.toList())

        override fun check(codeItems: ImmutableList<CodePeace>): Boolean {
            val values = indices.mapNotNull { index ->
                (codeItems.getOrNull(index) as? CodePeace.IntVariable)?.value
            }
            if (values.size != indices.size) return false
            return values == values.sortedDescending()
        }
    }
}
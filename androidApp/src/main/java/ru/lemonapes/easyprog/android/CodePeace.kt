package ru.lemonapes.easyprog.android

sealed class CodePeace {
    class IntVariable(val name: String, val value: Int?, val isMutable: Boolean = true) : CodePeace()
}
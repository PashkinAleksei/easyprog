package ru.lemonapes.easyprog

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
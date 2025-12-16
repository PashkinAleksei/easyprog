package ru.lemonapes.easyprog.android

import android.app.Application
import ru.lemonapes.easyprog.android.data.AppDatabase
import ru.lemonapes.easyprog.android.data.GameRepository

class EasyProgApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    val gameRepository: GameRepository by lazy {
        GameRepository(
            levelProgressDao = database.levelProgressDao(),
            savedCommandDao = database.savedCommandDao(),
        )
    }

    companion object {
        private lateinit var instance: EasyProgApplication

        fun getInstance(): EasyProgApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
package ru.lemonapes.easyprog.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import ru.lemonapes.easyprog.android.drag_and_drop_target.createGlobalDragAndDropTarget
import ru.lemonapes.easyprog.android.extensions.dragAndDropTextTarget
import ru.lemonapes.easyprog.android.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем edge-to-edge режим и скрываем системные бары
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            MyApplicationTheme {
                val globalDragAndDropTarget = remember {
                    viewModel.createGlobalDragAndDropTarget()
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .dragAndDropTextTarget(globalDragAndDropTarget),
                ) { paddings ->
                    MainScreen(
                        modifier = Modifier.padding(paddings),
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}
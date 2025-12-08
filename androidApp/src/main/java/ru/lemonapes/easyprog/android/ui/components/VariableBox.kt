package ru.lemonapes.easyprog.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.ui.theme.AppColors

@Composable
fun CodePeace.IntVariable.VariableBox(
    modifier: Modifier = Modifier
) {
    val color = AppColors.BoxColors[this.colorIndex % AppColors.BoxColors.size]

    Box {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.box),
            contentDescription = "Box",
            colorFilter = ColorFilter.tint(color, blendMode = BlendMode.Color),
        )
    }
}
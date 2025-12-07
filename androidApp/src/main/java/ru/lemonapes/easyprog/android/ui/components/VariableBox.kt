package ru.lemonapes.easyprog.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions

@Composable
fun CodePeace.IntVariable.VariableBox(modifier: Modifier = Modifier) {
    Box {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.box),
            contentDescription = "Box $name",
        )
        Surface(
            modifier = Modifier.align(Alignment.Center),
            color = AppColors.VariableLabelBackground,
            shape = RoundedCornerShape(AppDimensions.cornerRadius)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = AppDimensions.paddingSmall),
                text = name,
                textAlign = TextAlign.Center,
                color = AppColors.VariableLabelText
            )
        }
    }
}
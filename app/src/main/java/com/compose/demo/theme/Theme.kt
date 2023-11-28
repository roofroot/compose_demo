package com.compose.demo.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class CustomSystem(
    val id: Int, val name: String
)

val LocalCustomSystem = staticCompositionLocalOf {
    customSystem1
}

val customSystem1 = CustomSystem(
    id = 0, name = "theme1"
)
val customSystem2 = CustomSystem(
    id = 1, name = "theme2"
)
val customSystem3 = CustomSystem(
    id = 2, name = "theme3"
)

data class CustomColor(
    val colorBg: Color,
    val textColor: Color,
)

val LocalCustomColor = staticCompositionLocalOf {
    themeColor1
}
val themeColor1 = CustomColor(Color.Red, Color.Black)
val themeColor2 = CustomColor(Color.Blue, Color.White)
val themeColor3 = CustomColor(Color.Cyan, Color.Magenta)

@Composable
fun TestTheme(themeChangePage: @Composable (themeChangePage: ((theme: CustomSystem) -> Unit)) -> Unit) {
    val customSystem = remember {
        mutableStateOf(customSystem1)
    }
    val customColor = remember {
        mutableStateOf(themeColor1)
    }
    CompositionLocalProvider(
        LocalCustomSystem provides customSystem.value, LocalCustomColor provides customColor.value
    ) {
        themeChangePage({
            customSystem.value = it
            if (it.id == 0) {
                customColor.value = themeColor1
            } else if (it.id == 1) {
                customColor.value = themeColor2
            } else if (it.id == 2) {
                customColor.value = themeColor3
            }
        })
    }
}
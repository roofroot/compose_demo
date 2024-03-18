package com.compose.demo.theme

import androidx.annotation.DimenRes
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

data class CustomDimens(
    val mainPadding: Dp,
    val mainPageWidth: Dp,
)


val LocalCustomColor = staticCompositionLocalOf {
    themeColor1Dark
}
val themeColor1Dark = CustomColor(Color.Red, Color.Black)
val themeColor2Dark = CustomColor(Color.Blue, Color.White)
val themeColor3Dark = CustomColor(Color.Cyan, Color.Magenta)

val themeColor1Light = CustomColor(Color.Magenta, Color.White)
val themeColor2Light = CustomColor(Color.Blue, Color.White)
val themeColor3Light = CustomColor(Color.Cyan, Color.White)


val LocalCustomDimens = staticCompositionLocalOf {
    themeDimens1
}
val themeDimens1 = CustomDimens(mainPadding = 20.dp, mainPageWidth = 650.dp)
val themeDimens2 = CustomDimens(mainPadding = 10.dp, mainPageWidth = 450.dp)
val themeDimens3 = CustomDimens(mainPadding = 5.dp, mainPageWidth = 300.dp)

val LocalLanguage = staticCompositionLocalOf {
    Language.Chinese
}

enum class Language {
    English, Chinese
}

@Composable
fun TestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable (themeChange: ((theme: CustomSystem) -> Unit), changeLanguage: ((language: Language) -> Unit)) -> Unit
) {
    val customSystem = remember {
        mutableStateOf(customSystem1)
    }
    val localLanguage = remember {
        mutableStateOf(Language.Chinese)
    }
    val customColor = when (customSystem.value) {
        customSystem1 -> if (darkTheme) themeColor1Dark else themeColor1Light
        customSystem2 -> if (darkTheme) themeColor2Dark else themeColor2Light
        customSystem3 -> if (darkTheme) themeColor3Dark else themeColor3Light
        else -> if (darkTheme) themeColor1Dark else themeColor1Light
    }
    val customDimens = when (customSystem.value) {
        customSystem1 -> themeDimens1
        customSystem2 -> themeDimens2
        customSystem3 -> themeDimens3
        else -> themeDimens1
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = {
            CompositionLocalProvider(
                LocalCustomSystem provides customSystem.value,
                LocalCustomColor provides customColor,
                LocalCustomDimens provides customDimens,
                LocalLanguage provides localLanguage.value
            ) {
                content({
                    customSystem.value = it
                }, {
                    localLanguage.value = it
                })
            }
        }
    )
}
package com.compose.demo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.demo.nav.MyNavigation
import com.compose.demo.theme.CustomSystem
import com.compose.demo.theme.Language
import com.compose.demo.theme.LocalCustomColor
import com.compose.demo.theme.LocalCustomDimens
import com.compose.demo.theme.LocalLanguage
import com.compose.demo.theme.TestTheme
import com.compose.demo.theme.customSystem1
import com.compose.demo.theme.customSystem2
import com.compose.demo.theme.customSystem3

enum class ThemeNavTag {
    HOME, LOGIN, PAGE_ONE
}

@Composable
fun Home(
    navTo: (tag: String) -> Unit,
    navToPopup: (tag: String, popupTag: String, inclusive: Boolean) -> Unit
) {
    Column(
        Modifier
            .width(LocalCustomDimens.current.mainPageWidth)
            .padding(LocalCustomDimens.current.mainPadding)
            .background(LocalCustomColor.current.colorBg)
    ) {
        Text(text = "Home", fontSize = 30.sp)
        Button(onClick = {
            navTo(ThemeNavTag.LOGIN.name)
        }) {
            Text(text = "to Login")
        }
        Button(onClick = {
            navTo(ThemeNavTag.PAGE_ONE.name)
        }) {
            Text(text = "to PageOne")
        }
    }
}

@Composable
fun SimpleTheme() {
    val navController = rememberNavController()
    TestTheme { themeChange, languageChange ->

        NavHost(navController = navController, startDestination = ThemeNavTag.HOME.name) {
            composable(ThemeNavTag.HOME.name) {
                MyNavigation(controller = navController) { navTo, navPopupTo ->
                    Home(navTo = navTo, navToPopup = navPopupTo)
                }
            }
            composable(ThemeNavTag.LOGIN.name) {
                MyNavigation(controller = navController) { navTo, _ ->
                    Login(onNav = navTo)
                }
            }
            composable(ThemeNavTag.PAGE_ONE.name) {
                MyNavigation(controller = navController) { navTo, _ ->
                    PageOne(
                        onNav = navTo,
                        themeChange = themeChange,
                        changeLanguage = languageChange
                    )
                }

            }
        }
    }
}


@Composable
fun Login(onNav: (tag: String) -> Unit) {

    Column(
        Modifier
            .fillMaxSize()
            .background(LocalCustomColor.current.colorBg)
    ) {
        Text(text = "Login", fontSize = 30.sp)
        Button(onClick = {
            onNav(ThemeNavTag.HOME.name)
        }) {
            Text(text = "to Home")
        }
    }
}

@Composable
fun PageOne(
    onNav: (tag: String) -> Unit,
    themeChange: (theme: CustomSystem) -> Unit,
    changeLanguage: (language: Language) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(LocalCustomColor.current.colorBg)
    ) {
        Text(text = "PageOne", fontSize = 30.sp)

        Button(onClick = {
            onNav(ThemeNavTag.LOGIN.name)
        }) {
            Text(text = "to Login")
        }
        Button(onClick = {
            onNav(ThemeNavTag.HOME.name)
        }) {
            Text(text = "to Home")
        }
        Row {
            Button(onClick = { themeChange(customSystem1) }) {
                Text(
                    text = if (LocalLanguage.current == Language.Chinese) "主题一" else "theme one",
                    color = LocalCustomColor.current.textColor
                )
            }
            Button(onClick = { themeChange(customSystem2) }) {
                Text(
                    text = if (LocalLanguage.current == Language.Chinese) "主题二" else "theme two",
                    color = LocalCustomColor.current.textColor
                )
            }
            Button(onClick = { themeChange(customSystem3) }) {
                Text(
                    text = if (LocalLanguage.current == Language.Chinese) "主题三" else "theme three",
                    color = LocalCustomColor.current.textColor
                )
            }
        }
        Row {
            Button(onClick = { changeLanguage(Language.Chinese) }) {
                Text(
                    text = if (LocalLanguage.current == Language.Chinese) "中文" else "Chinese",
                    color = LocalCustomColor.current.textColor
                )
            }
            Button(onClick = { changeLanguage(Language.English) }) {
                Text(
                    text = if (LocalLanguage.current == Language.Chinese) "英文" else "English",
                    color = LocalCustomColor.current.textColor
                )
            }
        }
    }
}
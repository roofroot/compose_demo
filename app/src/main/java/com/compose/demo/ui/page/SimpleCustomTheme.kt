package com.compose.demo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.compose.demo.theme.LocalCustomColor
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
            .fillMaxSize()
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
    TestTheme { onThemeChange ->

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
                    PageOne(onNav = navTo, onThemeChange = onThemeChange)
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
    onNav: (tag: String) -> Unit, onThemeChange: (theme: CustomSystem) -> Unit
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
            Button(onClick = { onThemeChange(customSystem1) }) {
                Text(text = "主题一", color = LocalCustomColor.current.textColor)
            }
            Button(onClick = { onThemeChange(customSystem2) }) {
                Text(text = "主题二", color = LocalCustomColor.current.textColor)
            }
            Button(onClick = { onThemeChange(customSystem3) }) {
                Text(text = "主题三", color = LocalCustomColor.current.textColor)
            }
        }
    }
}
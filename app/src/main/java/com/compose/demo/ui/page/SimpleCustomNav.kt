package com.compose.demo.ui.page

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.demo.nav.MyNavigation
import com.compose.demo.nav.MyNavigation2
import com.compose.demo.nav.NavExtrasKey
import com.compose.demo.theme.CustomSystem
import com.compose.demo.theme.LocalCustomColor
import com.compose.demo.theme.TestTheme
import com.compose.demo.theme.customSystem1
import com.compose.demo.theme.customSystem2
import com.compose.demo.theme.customSystem3

enum class CustomNavTag {
    PageOne, PageTwo
}
@Composable
fun TestPageOne(
    navTo: (tag: String, bundle: Bundle?) -> Unit,
    navToPopup: (tag: String, popupTag: String, inclusive: Boolean) -> Unit,
    popupBack: (tag: String?, inclusive: Boolean, bundle: Bundle) -> Unit,
    back: NavBackStackEntry?,
) {
    Column(
        Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "${
                back?.savedStateHandle?.get<Bundle>(NavExtrasKey).toString()
            }", fontSize = 30.sp
        )
        Button(onClick = {
            val bundle = Bundle()
            bundle.putString("arg1", "我是PageOne传过来的参数" + System.currentTimeMillis())
            navTo(CustomNavTag.PageTwo.name, bundle)
        }) {
            Text(text = "to PageTwo")
        }
    }
}

@Composable
fun TestPageTwo(
    navTo: (tag: String, bundle: Bundle?) -> Unit,
    navToPopup: (tag: String, popupTag: String, inclusive: Boolean) -> Unit,
    popupBack: (tag: String?, inclusive: Boolean, bundle: Bundle) -> Unit,
    back: NavBackStackEntry?,
) {
    Column(
        Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "${
                back?.arguments?.getBundle(NavExtrasKey)
            }", fontSize = 30.sp
        )
        Button(onClick = {
            val bundle = Bundle()
            bundle.putString("arg1", "我是PageTwo返回的参数" + System.currentTimeMillis())
            popupBack(CustomNavTag.PageOne.name, false, bundle)
        }) {
            Text(text = "to Back")
        }
    }
}

@Composable
fun SimpleNav() {
    val navController = rememberNavController()
    TestTheme { onThemeChange ->
        NavHost(navController = navController, startDestination = CustomNavTag.PageOne.name) {
            composable(CustomNavTag.PageOne.name) {
                MyNavigation2(controller = navController) { navTo, navPopupTo, popBack ->
                    TestPageOne(navTo = navTo, navToPopup = navPopupTo, popupBack = popBack, it)
                }
            }
            composable(CustomNavTag.PageTwo.name) {
                MyNavigation2(controller = navController) { navTo, navPopupTo, popBack ->
                    TestPageTwo(navTo = navTo, navToPopup = navPopupTo, popupBack = popBack, it)
                }
            }
        }
    }
}



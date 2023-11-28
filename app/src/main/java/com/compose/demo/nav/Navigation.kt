package com.compose.demo.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun MyNavigation(
    controller: NavHostController,
    content: @Composable (navTo: (tag: String) -> Unit, navToPopup: (tag: String, popupTag: String, inclusive: Boolean) -> Unit) -> Unit
) {
    content({
        controller.navigate(it)
    }, { tag, popupTag, inclu ->
        controller.navigate(tag) {
            popUpTo(popupTag) {
                inclusive = inclu
            }
        }
    })
}
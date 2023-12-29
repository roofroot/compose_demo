package com.compose.demo.nav

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.get
import androidx.navigation.navArgument

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

val NavExtrasKey = "args"

@Composable
fun MyNavigation2(
    controller: NavHostController,
    content: @Composable (
        navTo: (tag: String, bundle: Bundle?) -> Unit,
        navToPopup: (tag: String, popupTag: String, inclusive: Boolean) -> Unit,
        navBackTo: (tag: String?, inclusive: Boolean, Bundle: Bundle?) -> Unit
    ) -> Unit
) {
    content({ tag, bundle ->
        bundle?.let {
            controller.graph.get(tag)
                .addArgument(
                    NavExtrasKey,
                    argument = navArgument(NavExtrasKey, { defaultValue = bundle }).argument
                )
        }
        controller.navigate(tag)
    }, { tag, popupTag, inclu ->
        controller.navigate(tag) {
            popUpTo(popupTag) {
                inclusive = inclu
            }
        }
    }, { tag, inclu, bundle ->
        bundle?.let { bundle ->
            tag?.let { tag ->
                controller.getBackStackEntry(tag).savedStateHandle.set(NavExtrasKey, bundle)
            }
        }
        if (tag != null) {
            controller.popBackStack(tag, inclu)
        } else {
            controller.popBackStack()
        }
    })
}
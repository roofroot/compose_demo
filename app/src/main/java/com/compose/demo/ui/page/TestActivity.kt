package com.compose.demo.ui.page

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.demo.nav.MyNavigation

enum class NavTag {
    NavPage, SimpleScrollView, SimpleCirculatePager,
    SimpleDraggableList,SimpleSeekbar,SimplePanelLayout,
    SimpleShape,SimpleTheme
}

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = NavTag.NavPage.name) {
                composable(NavTag.NavPage.name) {
                    MyNavigation(controller = navController) { navTo, _ ->
                        NavPage(navTo)
                    }
                }
                composable(NavTag.SimpleScrollView.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleScrollView()
                    }
                }
                composable(NavTag.SimpleCirculatePager.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SimpleCirculatePager()
                    }
                }
                composable(NavTag.SimpleDraggableList.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SimpleDraggableLazyList()
                    }
                }
                composable(NavTag.SimpleSeekbar.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SimpleSeekbar()
                    }
                }
                composable(NavTag.SimplePanelLayout.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SimplePanelLayout()
                    }
                }
                composable(NavTag.SimpleShape.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SimpleShape()
                    }
                }
                composable(NavTag.SimpleTheme.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SimpleTheme()
                    }
                }
            }
        }
    }
}

@Composable
fun NavPage(navTo: (tag: String) -> Unit) {
    Column {
        Button(onClick = { navTo(NavTag.SimpleScrollView.name) }) {
            Text(text = "ScrollView")
        }
        Button(onClick = { navTo(NavTag.SimpleCirculatePager.name) }) {
            Text(text = "CirculatePager")
        }
        Button(onClick = { navTo(NavTag.SimpleDraggableList.name) }) {
            Text(text = "DraggableLazyList")
        }
        Button(onClick = { navTo(NavTag.SimpleSeekbar.name) }) {
            Text(text = "Seekbar")
        }
        Button(onClick = { navTo(NavTag.SimplePanelLayout.name) }) {
            Text(text = "PanelLayout")
        }
        Button(onClick = { navTo(NavTag.SimpleShape.name) }) {
            Text(text = "CustomShape")
        }
        Button(onClick = { navTo(NavTag.SimpleTheme.name) }) {
            Text(text = "CustomTheme")
        }
    }
}
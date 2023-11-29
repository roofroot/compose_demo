package com.compose.demo.ui.page

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.demo.layout.PanelLayout
import com.compose.demo.nav.MyNavigation
import com.compose.demo.shape.WaveBorderShape
import com.compose.demo.ui.theme.black
import com.compose.demo.ui.theme.lavender
import com.compose.demo.ui.theme.lightpink
import com.compose.demo.ui.theme.mediumturquoise
import com.compose.demo.ui.theme.plum
import com.compose.demo.widget.GradientText

enum class NavTag {
    NavPage, SimpleScrollView, SimpleCirculatePager,
    SimpleDraggableList, SimpleSeekbar, SimplePanelLayout,
    SimpleShape, SimpleRefreshAndLoadMoreList
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
                composable(NavTag.SimpleRefreshAndLoadMoreList.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SimpleRefreshAndLoadList()
                    }
                }
            }
        }
    }
}

@Composable
fun NavPage(navTo: (tag: String) -> Unit) {
    LazyVerticalGrid(modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(5)) {
        items(8) {
            when (it) {
                0 -> {
                    Cards(
                        navTag = NavTag.SimpleRefreshAndLoadMoreList,
                        text = "RefreshAndLoadMoreList",
                        navTo = navTo
                    )
                }

                1 -> {
                    Cards(navTag = NavTag.SimpleScrollView, text = "ScrollView", navTo = navTo)
                }

                2 -> {
                    Cards(
                        navTag = NavTag.SimpleCirculatePager,
                        text = "CirculatePager",
                        navTo = navTo
                    )
                }

                3 -> {
                    Cards(
                        navTag = NavTag.SimpleDraggableList,
                        text = "DraggableLazyList",
                        navTo = navTo
                    )
                }

                4 -> {
                    Cards(navTag = NavTag.SimpleSeekbar, text = "Seekbar", navTo = navTo)
                }

                5 -> {
                    Cards(navTag = NavTag.SimplePanelLayout, text = "PanelLayout", navTo = navTo)
                }

                6 -> {
                    Cards(navTag = NavTag.SimpleScrollView, text = "ScrollView", navTo = navTo)
                }

                7 -> {
                    Cards(navTag = NavTag.SimpleShape, text = "CustomShape", navTo = navTo)
                }
            }


        }
    }
}

@Composable
fun Cards(navTag: NavTag, text: String, navTo: (tag: String) -> Unit) {
    Box(
        Modifier
            .size(200.dp)
            .padding(10.dp)
            .background(brush = Brush.sweepGradient(listOf(plum, lavender, mediumturquoise, plum)))
            .shadow(10.dp)
            .clickable {
                navTo(navTag.name)
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White, shape = WaveBorderShape(10.dp, 5.dp)),
            contentAlignment = Alignment.Center
        ) {
            GradientText(
                modifier = Modifier
                    .width(150.dp)
                    .height(80.dp), text = text, brash = Brush.linearGradient(
                    listOf(lightpink, black, lightpink)
                )
            )
        }
    }
}
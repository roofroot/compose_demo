package com.compose.demo.ui.page

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.demo.nav.MyNavigation
import com.compose.demo.shape.WaveBorderShape
import com.compose.demo.ui.theme.black
import com.compose.demo.ui.theme.lavender
import com.compose.demo.ui.theme.mediumturquoise
import com.compose.demo.ui.theme.pink
import com.compose.demo.ui.theme.plum
import com.compose.demo.widget.GradientText
import com.desaysv.hmicomponents.compose.SampleDraggableInsertLazyList

enum class NavTag {
    NavPage, SampleScrollView, SampleCirculatePager,
    SampleDraggableList, SampleSeekbar, SamplePanelLayout,
    SampleShape, SampleRefreshAndLoadMoreList, SampleDraggableInsertList, SampleSelectableList,
    SampleCustomTabRow, SampleDropDownList, SampleCurveChart, SampleCircleProgress, SampleCustomTheme,
    SampleGridView, SampleCalendarView, SampleNav, SampleCustomTabColumn, SampleScrollBar, SampleBlurModifier
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
                composable(NavTag.SampleScrollView.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleScrollView()
                    }
                }
                composable(NavTag.SampleCirculatePager.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleCirculatePager()
                    }
                }
                composable(NavTag.SampleDraggableList.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleDraggableLazyList()
                    }
                }
                composable(NavTag.SampleSeekbar.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleSeekbar()
                    }
                }
                composable(NavTag.SamplePanelLayout.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SamplePanelLayout()
                    }
                }
                composable(NavTag.SampleShape.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleShape()
                    }
                }
                composable(NavTag.SampleRefreshAndLoadMoreList.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleRefreshAndLoadList()
                    }
                }
                composable(NavTag.SampleDraggableInsertList.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleDraggableInsertLazyList()
                    }
                }
                composable(NavTag.SampleSelectableList.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleSelectableList()
                    }
                }
                composable(NavTag.SampleCustomTabRow.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleCustomTabRow()
                    }
                }
                composable(NavTag.SampleDropDownList.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleDropDownList()
                    }
                }
                composable(NavTag.SampleCurveChart.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleCurveChart()
                    }
                }
                composable(NavTag.SampleCircleProgress.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleCircleProgress()
                    }
                }
                composable(NavTag.SampleCustomTheme.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleTheme()
                    }
                }

                composable(NavTag.SampleGridView.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleGridView()
                    }
                }
                composable(NavTag.SampleCalendarView.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleCalendarView()
                    }
                }
                composable(NavTag.SampleNav.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleNav()
                    }
                }
                composable(NavTag.SampleCustomTabColumn.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleCustomTabColumn()
                    }
                }

                composable(NavTag.SampleScrollBar.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleScrollBar()
                    }
                }

                composable(NavTag.SampleBlurModifier.name) {
                    MyNavigation(controller = navController) { _, _ ->
                        SampleBlurModifier()
                    }
                }

            }

        }
    }
}


@Composable
fun NavPage(navTo: (tag: String) -> Unit) {

    LazyVerticalGrid(modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(5)) {
        items(20) {
            when (it) {
                0 -> {
                    Cards(
                        navTag = NavTag.SampleRefreshAndLoadMoreList,
                        text = "RefreshAndLoadMoreList",
                        navTo = navTo
                    )
                }

                1 -> {
                    Cards(navTag = NavTag.SampleScrollView, text = "ScrollView", navTo = navTo)
                }

                2 -> {
                    Cards(
                        navTag = NavTag.SampleCirculatePager,
                        text = "CirculatePager",
                        navTo = navTo
                    )
                }

                3 -> {
                    Cards(
                        navTag = NavTag.SampleDraggableList,
                        text = "DraggableLazyList",
                        navTo = navTo
                    )
                }

                4 -> {
                    Cards(navTag = NavTag.SampleSeekbar, text = "Seekbar", navTo = navTo)
                }

                5 -> {
                    Cards(navTag = NavTag.SamplePanelLayout, text = "PanelLayout", navTo = navTo)
                }

                6 -> {
                    Cards(
                        navTag = NavTag.SampleCircleProgress,
                        text = "CircleProgress",
                        navTo = navTo
                    )
                }

                7 -> {
                    Cards(
                        navTag = NavTag.SampleShape,
                        text = "CustomShape",
                        navTo = navTo
                    )
                }

                8 -> {
                    Cards(
                        navTag = NavTag.SampleDraggableInsertList,
                        text = "DraggableInsertList",
                        navTo = navTo
                    )
                }

                9 -> {
                    Cards(
                        navTag = NavTag.SampleSelectableList,
                        text = "SelectableList",
                        navTo = navTo
                    )
                }

                10 -> {
                    Cards(
                        navTag = NavTag.SampleCustomTabRow,
                        text = "CustomTabRow",
                        navTo = navTo
                    )
                }

                11 -> {
                    Cards(
                        navTag = NavTag.SampleDropDownList,
                        text = "DropDownList",
                        navTo = navTo
                    )
                }

                12 -> {
                    Cards(
                        navTag = NavTag.SampleCurveChart,
                        text = "CurveChart",
                        navTo = navTo
                    )
                }

                13 -> {
                    Cards(
                        navTag = NavTag.SampleCustomTheme,
                        text = "CustomTheme",
                        navTo = navTo
                    )
                }

                14 -> {
                    Cards(
                        navTag = NavTag.SampleGridView,
                        text = "GridView",
                        navTo = navTo
                    )
                }

                15 -> {
                    Cards(
                        navTag = NavTag.SampleCalendarView,
                        text = "CalendarView",
                        navTo = navTo
                    )
                }

                16 -> {
                    Cards(
                        navTag = NavTag.SampleNav,
                        text = "SampleNav",
                        navTo = navTo
                    )
                }

                17 -> {
                    Cards(
                        navTag = NavTag.SampleCustomTabColumn,
                        text = "SampleCustomTabColumn",
                        navTo = navTo
                    )
                }

                18 -> {
                    Cards(
                        navTag = NavTag.SampleScrollBar,
                        text = "SampleScrollBar",
                        navTo = navTo
                    )
                }

                19 -> {
                    Cards(
                        navTag = NavTag.SampleBlurModifier,
                        text = "SampleBlurModifier",
                        navTo = navTo
                    )
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
                .clip(shape = WaveBorderShape(10.dp, 5.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            GradientText(
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp),
                text = text,
                brashColors =
                listOf(pink, black, pink),
                maxLines = 1,
            )
        }
    }
}
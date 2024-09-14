package com.compose.demo.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compose.demo.shape.FlowerShape
import com.compose.demo.shape.WaveBorderShape
import com.compose.demo.shape.pentagramShape
import com.compose.demo.ui.theme.pink
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SampleShape() {
    val pageState = rememberPagerState{3}
    val scope = rememberCoroutineScope()
    Column {
        TabRow(selectedTabIndex = pageState.currentPage) {
            Tab(selected = pageState.currentPage == 0, onClick = {
                scope.launch {
                    pageState.scrollToPage(0)
                }
            }) {
                Text(text = "波浪边框矩形")
            }
            Tab(selected = pageState.currentPage == 1, onClick = {
                scope.launch {
                    pageState.scrollToPage(1)
                }
            }) {
                Text(text = "五角星")
            }
            Tab(selected = pageState.currentPage == 2, onClick = {
                scope.launch {
                    pageState.scrollToPage(2)
                }
            }) {
                Text(text = "花朵形")
            }

        }
        HorizontalPager(state = pageState) {
            when (it) {
                0 -> {
                    SampleWaveBoarderShape()
                }

                1 -> {
                    SamplePentagramShape()
                }

                2 -> {
                    SampleFlowerShape()
                }
            }
        }
    }

}

@Composable
fun SampleWaveBoarderShape() {
    Row() {
        Box(
            modifier = Modifier
                .size(300.dp)
                .border(
                    2.dp,
                    color = pink,
                    shape = WaveBorderShape(waveHeight = 4.dp, waveWidth = 20.dp)
                )
        ) {

        }
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    color = getRandomColor(),
                    WaveBorderShape(waveHeight = 2.dp, waveWidth = 5.dp)
                )
        ) {

        }
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(400.dp)
                .background(
                    color = getRandomColor(),
                    WaveBorderShape(waveHeight = 4.dp, waveWidth = 15.dp)
                )
        ) {

        }
    }
}

@Composable
fun SamplePentagramShape() {
    Row {
        Box(
            modifier = Modifier
                .size(300.dp)
                .border(
                    2.dp,
                    color = getRandomColor(),
                    shape = pentagramShape()
                )
        ) {

        }
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(color = getRandomColor(), pentagramShape())
        ) {

        }
    }
}

@Composable
fun SampleFlowerShape() {
    Row {
        Box(
            Modifier
                .size(300.dp)
                .border(
                    1.dp,
                    getRandomColor(),
                    shape = FlowerShape(waveWidth = 30.dp, waveHeight = 10.dp)
                )
        ) {

        }
        Box(
            Modifier
                .size(300.dp)
                .background(
                    getRandomColor(),
                    shape = FlowerShape(waveWidth = 80.dp, waveHeight = 100.dp)
                )
        ) {

        }
        Box(
            Modifier
                .size(300.dp)
                .background(
                    getRandomColor(),
                    shape = FlowerShape(
                        waveCount = 8,
                        waveWidth = 80.dp,
                        waveHeight = 50.dp
                    )
                )
        ) {

        }
    }
}

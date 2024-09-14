package com.compose.demo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compose.demo.layout.ScrollView
import kotlin.random.Random


@Composable
fun SampleScrollView() {
    ScrollView(modifier = Modifier.fillMaxSize(), scrollBarHeight = 100.dp, scrollBarWidth = 10.dp,
        scrollBarColor = Color.Cyan) {
        Column {
            items()
        }
    }
}
@Composable
fun items() {
    for (i in 1..10) {
        val h = Random.nextInt(100, 200).dp
        Box(
            Modifier
                .height(h)
                .fillMaxWidth()
                .background(getRandomColor())
        ) {

        }
    }
}
fun getRandomColor(): Color {
    return Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
}
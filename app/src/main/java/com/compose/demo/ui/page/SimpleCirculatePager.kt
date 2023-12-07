package com.compose.demo.ui.page

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.layout.AnimPageIndicator
import com.compose.demo.layout.CirculatePager
import com.compose.demo.layout.OffsetPagerIndicator
import com.compose.demo.layout.SelectPagerIndicator
import com.compose.demo.layout.getCirclePagerState

import com.google.accompanist.coil.rememberCoilPainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimpleCirculatePager() {
    val list = listOf(
        R.mipmap.img_0,
        R.mipmap.img_1,
        R.mipmap.img_2,
        R.mipmap.img_3,
        R.mipmap.img_4,
        R.mipmap.img_5
    )
    val autoScroll = remember {
        mutableStateOf(false)
    }
    val pagerState = getCirclePagerState()
    Box(modifier = Modifier.fillMaxSize()) {
        CirculatePager(
            data = list,
            autoScrollState = autoScroll,
            pagerState = pagerState
        ) { item, pos ->
            val coilPainter = rememberCoilPainter(request = item)
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = coilPainter,
                contentDescription = ""
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SelectPagerIndicator(
                isCirculate = true,
                count = list.size,
                state = pagerState
            ) { isSelected, index ->
                val color = remember {
                    mutableStateOf(if (isSelected) Color.Blue else Color.Gray)
                }
                val animColor = animateColorAsState(targetValue = color.value)
                if (isSelected) color.value = Color.Blue else color.value = Color.Gray
                Box(
                    Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(animColor.value)
                ) {

                }
            }

            SelectPagerIndicator(
                isCirculate = true,
                count = list.size,
                state = pagerState
            ) { isSelected, index ->
                val width = remember {
                    mutableStateOf(if (isSelected) 10.dp else 5.dp)
                }
                val animWidth = animateDpAsState(width.value)
                if (isSelected) {
                    width.value = 10.dp
                } else {
                    width.value = 5.dp
                }
                Box(
                    Modifier
                        .width(animWidth.value)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.Blue else Color.Gray)
                ) {

                }
            }
            AnimPageIndicator(count = list.size, pagerState = pagerState, isCirculate = true)

            OffsetPagerIndicator(
                count = list.size,
                pagerState = pagerState,
                isCirculate = true,
                contentWidth = 5 * LocalDensity.current.density,
                content = {
                    Box(
                        Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    ) {

                    }
                }) {
                Box(
                    Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color.Blue)
                ) {

                }
            }

            Button(onClick = { autoScroll.value = !autoScroll.value }) {
                Text(text = "自动滚动开关")
            }
        }
    }
}

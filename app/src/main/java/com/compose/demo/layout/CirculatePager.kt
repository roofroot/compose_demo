package com.compose.demo.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> CirculatePager(
    data: List<T>,
    autoScrollState: MutableState<Boolean>,
    pagerState: PagerState = rememberPagerState { Int.MAX_VALUE },
    item: @Composable (t: T, pos: Int) -> Unit,
) {

    val scope = rememberCoroutineScope()
    val scopeAutoScroll = rememberCoroutineScope()
    val isOnAnim = remember {
        mutableStateOf(false)
    }
    HorizontalPager(state = pagerState) {
        val pos = it % data.size
        item(data.get(pos), pos)
    }
    if (autoScrollState.value) {
        if (!isOnAnim.value) {
            isOnAnim.value = true
            //避免重复启动
            scopeAutoScroll.launch {
                while (autoScrollState.value) {
                    delay(3000)
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }.invokeOnCompletion {
                isOnAnim.value = false
            }
        }
        scope.launch {
            pagerState.interactionSource.interactions.collect {
                when (it) {
                    is PressInteraction.Press -> {
                        scopeAutoScroll.cancel()
                        //在按下手指时停止自动滚动
                    }

                    is PressInteraction.Release -> {
                        scopeAutoScroll.launch {
                            if (isOnAnim.value == false) {
                                isOnAnim.value = true
                                while (autoScrollState.value) {
                                    delay(3000)
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                        //在抬起手指时如果当前没有自动滚动，开始自动滚动
                    }

                    is PressInteraction.Cancel -> {
                        scopeAutoScroll.launch {
                            if (isOnAnim.value == false) {
                                isOnAnim.value = true
                                while (autoScrollState.value) {
                                    delay(3000)
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                        //在抬起手指时如果当前没有自动滚动，开始自动滚动
                    }
                }
            }
        }
    }
    //对滑动到int最大值和0时做一下处理，虽然实际只用中，应该很少有机会会执行这段代码
    if (pagerState.currentPage == Int.MAX_VALUE) {
        scope.launch {
            pagerState.scrollToPage(1000 * data.size + pagerState.currentPage)
        }
    } else if (pagerState.currentPage == 0) {
        scope.launch {
            pagerState.scrollToPage(1000 * data.size)
        }
    }
}
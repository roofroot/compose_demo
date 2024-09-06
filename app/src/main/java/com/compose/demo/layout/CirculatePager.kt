package com.compose.demo.layout

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> CirculatePager(
    data: List<T>,
    autoScrollState: Boolean,
    pagerState: PagerState = getCirclePagerState(),
    item: @Composable (t: T, pos: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val isOnUserInput = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(autoScrollState) {

        while (autoScrollState) {
            try {
                delay(3000)
                Log.e("autoScrollState", "${pagerState.isScrollInProgress},${isOnUserInput.value}")
                if (!isOnUserInput.value && !pagerState.isScrollInProgress) {
                    scope.launch {
                        //需要放到scope防止出现异常，导致循环退出
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            } catch (e: Exception) {
                Log.e("autoScrollState", "${e.message}")
            }
        }

    }
    LaunchedEffect(key1 = Unit) {
        pagerState.interactionSource.interactions.collect {
            when (it) {
                is DragInteraction.Start -> {
                    isOnUserInput.value = true
                    //在按下手指时停止自动滚动
                }

                is DragInteraction.Stop -> {
                    isOnUserInput.value = false
                    //在抬起手指时如果当前没有自动滚动，开始自动滚动
                }

                is DragInteraction.Cancel -> {
                    isOnUserInput.value = false
                    //在抬起手指时如果当前没有自动滚动，开始自动滚动
                }
            }
        }
    }
    HorizontalPager(modifier = Modifier.motionEventSpy {
        if (it.action == MotionEvent.ACTION_CANCEL
            || it.action == MotionEvent.ACTION_OUTSIDE
            || it.action == MotionEvent.ACTION_UP
        ) {
            isOnUserInput.value = false
        }
    }, state = pagerState) {
        val pos = it % data.size
        item(data[pos], pos)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun getCirclePagerState(): PagerState {
    return rememberPagerState {
        Int.MAX_VALUE
    }
}
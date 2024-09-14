package com.compose.demo.layout

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectPagerIndicator(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5.dp),
    count: Int,
    state: PagerState,
    isCirculate: Boolean = false,
    content: @Composable (isSelected: Boolean, index: Int) -> Unit
) {
    LazyRow(modifier = modifier, content = {
        items(count) {
            val currentIndex = if (isCirculate) state.currentPage % count else state.currentPage
            content(currentIndex == it, it)
        }
    }, horizontalArrangement = horizontalArrangement)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OffsetPagerIndicator(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5.dp),
    count: Int,
    pagerState: PagerState,
    isCirculate: Boolean = false,
    contentWidth: Float,
    content: @Composable () -> Unit,
    offsetContent: @Composable () -> Unit
) {
    val offsetWidth = remember {
        mutableStateOf(10)
    }
    var currentIndex = if (isCirculate) pagerState.currentPage % count else pagerState.currentPage
    var startIndex = if (isCirculate) pagerState.settledPage % count else pagerState.settledPage
    Box {
        LazyRow(modifier = modifier
            .onPlaced {
                offsetWidth.value =
                    ((it.size.width.toFloat() - contentWidth) / (count - 1)).roundToInt()
            }, content = {
            items(count) {
                content()
            }
        }, horizontalArrangement = horizontalArrangement
        )
        if (isCirculate && pagerState.targetPage % count == 0 && pagerState.settledPage % count != 1) {
            Box(
                Modifier
                    .offset { IntOffset(0, 0) }
                    .wrapContentSize(),
                contentAlignment = Alignment.Center
            ) {
                offsetContent()
            }
        } else if (isCirculate && pagerState.targetPage % count == count - 1 && pagerState.settledPage % count != count - 2) {
            val offset = (count - 1) * offsetWidth.value
            Box(
                Modifier
                    .offset { IntOffset(offset, 0) }
                    .wrapContentSize(),
                contentAlignment = Alignment.Center
            ) {
                offsetContent()
            }
        } else {
            val startOffset = startIndex * offsetWidth.value
            val offsetX = remember {
                mutableStateOf(startOffset)
            }
            val animOffset = animateIntAsState(offsetX.value)

            if (pagerState.targetPage > pagerState.settledPage || pagerState.settledPage < pagerState.currentPage) {
                //右滑，此时currentPageOffsetFraction的值从0->0.5再从-0.5->0
                if (pagerState.currentPageOffsetFraction > 0) {
                    offsetX.value =
                        (startOffset + pagerState.currentPageOffsetFraction * offsetWidth.value).roundToInt()
                } else if (pagerState.currentPageOffsetFraction < 0) {
                    offsetX.value =
                        (startOffset + (offsetWidth.value + pagerState.currentPageOffsetFraction * offsetWidth.value)).roundToInt()
                } else {
                    offsetX.value = currentIndex * offsetWidth.value
                }
            } else if (pagerState.targetPage < pagerState.settledPage || pagerState.settledPage > pagerState.currentPage) {
                //左滑，此时currentPageOffsetFraction的值从0 -> -0.5再从0.5 -> 0
                if (pagerState.currentPageOffsetFraction > 0) {
                    offsetX.value =
                        (startOffset - (offsetWidth.value - pagerState.currentPageOffsetFraction * offsetWidth.value)).roundToInt()
                } else if (pagerState.currentPageOffsetFraction < 0) {
                    offsetX.value =
                        (startOffset + pagerState.currentPageOffsetFraction * offsetWidth.value).roundToInt()
                } else {
                    offsetX.value = currentIndex * offsetWidth.value
                }
            } else {
                //其他情况，主要用于滑到一半松手的情况
                offsetX.value = currentIndex * offsetWidth.value
            }
            Box(
                Modifier
                    .offset { IntOffset(animOffset.value, 0) }
                    .wrapContentSize(),
                contentAlignment = Alignment.Center
            ) {
                offsetContent()
            }
        }

    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimPageIndicator(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5.dp),
    count: Int,
    pagerState: PagerState,
    isCirculate: Boolean = false
) {

    SelectPagerIndicator(
        modifier = modifier, isCirculate = true, count = count, state = pagerState
    ) { isSelected, index ->
        val width = remember {
            mutableStateOf(if (isSelected) 10.dp else 5.dp)
        }
        val animWidth = animateDpAsState(width.value)
        val currentIndex =
            if (isCirculate) pagerState.targetPage % count else pagerState.currentPage
        if (currentIndex != index && width.value > 5.dp) {
            if (pagerState.targetPage > pagerState.settledPage || pagerState.settledPage < pagerState.currentPage) {
                if (pagerState.currentPageOffsetFraction > 0) {
                    width.value = 10.dp - (pagerState.currentPageOffsetFraction * 5).dp
                } else if (pagerState.currentPageOffsetFraction < 0) {
                    width.value = 10.dp - (5 + pagerState.currentPageOffsetFraction * 5).dp
                } else {
                    if (isSelected) {
                        width.value = 10.dp
                    } else {
                        width.value = 5.dp
                    }
                }
            } else if (pagerState.targetPage < pagerState.settledPage || pagerState.settledPage > pagerState.currentPage) {
                if (pagerState.currentPageOffsetFraction > 0) {
                    width.value = 10.dp - (5 - (pagerState.currentPageOffsetFraction * 5)).dp
                } else if (pagerState.currentPageOffsetFraction < 0) {
                    width.value = 10.dp + (pagerState.currentPageOffsetFraction * 5).dp
                } else {
                    if (isSelected) {
                        width.value = 10.dp
                    } else {
                        width.value = 5.dp
                    }
                }
            } else {
                if (isSelected) {
                    width.value = 10.dp
                } else {
                    width.value = 5.dp
                }
            }
        }
        if (currentIndex == index && width.value < 10.dp) {
            if (pagerState.targetPage > pagerState.settledPage || pagerState.settledPage < pagerState.currentPage) {
                if (pagerState.currentPageOffsetFraction > 0) {
                    width.value = 5.dp + (pagerState.currentPageOffsetFraction * 5).dp
                } else if (pagerState.currentPageOffsetFraction < 0) {
                    width.value = 5.dp + (5 + pagerState.currentPageOffsetFraction * 5).dp
                } else {
                    if (isSelected) {
                        width.value = 10.dp
                    } else {
                        width.value = 5.dp
                    }
                }
            } else if (pagerState.targetPage < pagerState.settledPage || pagerState.settledPage > pagerState.currentPage) {
                if (pagerState.currentPageOffsetFraction > 0) {
                    width.value = 5.dp + (5 - (pagerState.currentPageOffsetFraction * 5)).dp
                } else if (pagerState.currentPageOffsetFraction < 0) {
                    width.value = 5.dp - (pagerState.currentPageOffsetFraction * 5).dp
                } else {
                    if (isSelected) {
                        width.value = 10.dp
                    } else {
                        width.value = 5.dp
                    }
                }
            } else {
                if (isSelected) {
                    width.value = 10.dp
                } else {
                    width.value = 5.dp
                }
            }
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

}
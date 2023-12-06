package com.compose.demo.layout

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun <T> CustomTabRow(
    modifier: Modifier = Modifier,
    selectedIndex: MutableState<Int>,
    data: List<T>,
    indicatorContent: @Composable () -> Unit,
    indicatorAlignment: Alignment = Alignment.BottomCenter,
    autoFixedContent: Boolean = false,
    placeCount: Int = -1,
    autoScroll: Boolean = false,
    frontIndicator: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceAround,
    tabContent: @Composable (item: T, index: Int, selected: Boolean) -> Unit
) {
    val contentSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val rellyContentSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val indicatorOffsetX = remember {
        mutableStateOf(0)
    }
    val layoutSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val listState = rememberLazyListState()
    val density = LocalDensity.current.density
    val animIndicatorOffsetX =
        animateIntAsState(targetValue = indicatorOffsetX.value)
    if (listState.isScrollInProgress) {
        if (listState.firstVisibleItemIndex <= selectedIndex.value && selectedIndex.value <=
            listState.firstVisibleItemIndex + listState.layoutInfo.viewportSize.width / contentSize.value.width
        ) {
            indicatorOffsetX.value =
                listState.layoutInfo.visibleItemsInfo.get(selectedIndex.value - listState.firstVisibleItemIndex).offset
        } else {
            if (indicatorOffsetX.value < listState.firstVisibleItemIndex) {
                indicatorOffsetX.value = -contentSize.value.width
            } else {
                indicatorOffsetX.value = listState.layoutInfo.viewportSize.width
            }
        }
    }
    Box(
        modifier
            .wrapContentSize()
            .onPlaced {
                layoutSize.value = it.size
            }) {
        if (!frontIndicator) {
            Box(
                Modifier
                    .width((contentSize.value.width / LocalDensity.current.density).dp)
                    .height((contentSize.value.height / LocalDensity.current.density).dp)
                    .offset {
                        IntOffset(
                            if (listState.isScrollInProgress) indicatorOffsetX.value else animIndicatorOffsetX.value,
                            0
                        )
                    }, contentAlignment = indicatorAlignment
            ) {
                indicatorContent.invoke()
            }
        }

        val scope = rememberCoroutineScope()

        LazyRow(
            Modifier
                .fillMaxSize()
                .onPlaced {
                    if (listState.firstVisibleItemIndex == 0 && selectedIndex.value == 0) {
                        indicatorOffsetX.value =
                            listState.layoutInfo.visibleItemsInfo.get(selectedIndex.value - listState.firstVisibleItemIndex).offset
                    }
                },
            horizontalArrangement = horizontalArrangement,
            state = listState,
            content = {
                var modifier = Modifier
                    .onPlaced {
                        contentSize.value = it.size
                    }

                if (autoFixedContent) {
                    if (placeCount == -1) {
                        modifier = modifier
                            .onPlaced {
                                contentSize.value = it.size
                            }
                            .width(((layoutSize.value.width / data.count()) / density).dp)
                            .wrapContentHeight()
                    } else {
                        modifier = modifier
                            .onPlaced {
                                contentSize.value = it.size
                            }
                            .width(((layoutSize.value.width / placeCount) / density).dp)
                            .wrapContentHeight()
                    }
                } else {
                    modifier = modifier.wrapContentSize()
                }
                itemsIndexed(data) { index, item ->
                    Box(
                        modifier
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    selectedIndex.value = index
                                    indicatorOffsetX.value =
                                        listState.layoutInfo.visibleItemsInfo.get(
                                            selectedIndex.value - listState.firstVisibleItemIndex
                                        ).offset
                                    if (autoScroll) {
                                        scope.launch {
                                            listState.animateScrollToItem(
                                                selectedIndex.value,
                                                -listState.layoutInfo.viewportSize.width / 2 + contentSize.value.width / 2
                                            )
                                            indicatorOffsetX.value =
                                                listState.layoutInfo.visibleItemsInfo.get(
                                                    selectedIndex.value - listState.firstVisibleItemIndex
                                                ).offset
                                        }
                                    }
                                })
                            }, contentAlignment = Alignment.Center
                    ) {
                        val select = selectedIndex.value == index

                        tabContent.invoke(item, index, select)

                        if (select) {
                            try {
                                indicatorOffsetX.value =
                                    listState.layoutInfo.visibleItemsInfo.get(
                                        selectedIndex.value - listState.firstVisibleItemIndex
                                    ).offset
                            } catch (e: Exception) {

                            }
                        }
                    }
                }
            })
        if (frontIndicator) {
            Box(
                Modifier
                    .width((contentSize.value.width / LocalDensity.current.density).dp)
                    .height((contentSize.value.height / LocalDensity.current.density).dp)
                    .offset {
                        IntOffset(
                            if (listState.isScrollInProgress) indicatorOffsetX.value else animIndicatorOffsetX.value,
                            0
                        )
                    }, contentAlignment = indicatorAlignment
            ) {
                indicatorContent.invoke()
            }
        }


    }
}
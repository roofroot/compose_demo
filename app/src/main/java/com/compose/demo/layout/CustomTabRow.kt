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
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
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

/**
 * @param modifier Modifier
 * @param selectedIndex MutableState<Int> 当前选中的index
 * @param data List<T> 数据列表
 * @param indicatorContent [@androidx.compose.runtime.Composable] Function0<Unit> 自定义指示器
 * @param indicatorAlignment Alignment 指示器的对齐方式
 * @param autoFixedContent Boolean 是否自适应大小，如果设置为true ,配合 place count会自动计算每个 item的大小,不设置placeCount,根据data的size计算
 * @param placeCount Int 用于autoFixedContent为true时计算大小，控件一页展示的数据总数
 * @param autoScroll Boolean 是否自动滚动，用于列表数据超过屏幕时是否自动滚动的设置
 * @param frontIndicator Boolean 指定指示器是在item前景还是背景
 * @param horizontalArrangement Horizontal item的对齐方式
 * @param tabContent [@androidx.compose.runtime.Composable] Function3<[@kotlin.ParameterName] T, [@kotlin.ParameterName] Int, [@kotlin.ParameterName] Boolean, Unit>
 *     item的样式
 */
@Composable
fun <T> CustomTabRow(
    modifier: Modifier = Modifier.wrapContentSize(),
    tabRowModifier: Modifier = Modifier,
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
        mutableStateMapOf<Int, IntSize>()
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
        if (selectedIndex.value - listState.firstVisibleItemIndex > 0 && selectedIndex.value - listState.firstVisibleItemIndex
            < listState.layoutInfo.visibleItemsInfo.size - 1
        ) {
            indicatorOffsetX.value =
                listState.layoutInfo.visibleItemsInfo.get(selectedIndex.value - listState.firstVisibleItemIndex).offset
        } else {
            if (indicatorOffsetX.value < listState.firstVisibleItemIndex) {

                indicatorOffsetX.value = -(contentSize[selectedIndex.value]?.width?.toInt() ?: 0)

            } else {
                indicatorOffsetX.value = listState.layoutInfo.viewportSize.width
            }
        }
    }
    Box(
        modifier
            .onPlaced {
                layoutSize.value = it.size
            }) {
        if (!frontIndicator) {
            Box(
                Modifier
                    .width(((contentSize[selectedIndex.value]?.width?:100) / LocalDensity.current.density).dp)
                    .height(((contentSize[selectedIndex.value]?.height?:100) / LocalDensity.current.density).dp)
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
            tabRowModifier
                .onPlaced {
                    if (listState.firstVisibleItemIndex == 0 && selectedIndex.value == 0) {
                        indicatorOffsetX.value =
                            listState.layoutInfo.visibleItemsInfo[selectedIndex.value - listState.firstVisibleItemIndex].offset
                    }
                },
            horizontalArrangement = horizontalArrangement,
            state = listState,
            content = {
                var modifier = Modifier.wrapContentHeight()

                if (autoFixedContent) {
                    if (placeCount == -1) {
                        modifier = modifier

                            .width(((layoutSize.value.width / data.count()) / density).dp)
                            .wrapContentHeight()
                    } else {
                        modifier = modifier
                            .width(((layoutSize.value.width / placeCount) / density).dp)
                            .wrapContentHeight()
                    }
                } else {
                    modifier = modifier.wrapContentSize()
                }
                itemsIndexed(data) { index, item ->
                    Box(
                        modifier
                            .onPlaced {
                                contentSize.put(index, it.size)
                            }
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
                                                -listState.layoutInfo.viewportSize.width / 2 + (contentSize[selectedIndex.value]?.width?:100) / 2
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
                    .width(((contentSize[selectedIndex.value]?.width?:100) / LocalDensity.current.density).dp)
                    .height(((contentSize[selectedIndex.value]?.height?:100) / LocalDensity.current.density).dp)
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
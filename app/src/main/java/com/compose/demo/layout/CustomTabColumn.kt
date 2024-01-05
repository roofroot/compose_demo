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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
 * @param verticalArrangement Arrangement.Vertical item的对齐方式
 * @param tabContent [@androidx.compose.runtime.Composable] Function3<[@kotlin.ParameterName] T, [@kotlin.ParameterName] Int, [@kotlin.ParameterName] Boolean, Unit>
 *     item的样式
 */
@Composable
fun <T> CustomTabColumn(
    modifier: Modifier = Modifier,
    selectedIndex: MutableState<Int>,
    data: List<T>,
    indicatorContent: @Composable () -> Unit,
    indicatorAlignment: Alignment = Alignment.CenterStart,
    autoFixedContent: Boolean = false,
    placeCount: Int = -1,
    autoScroll: Boolean = false,
    frontIndicator: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceAround,
    tabContent: @Composable (item: T, index: Int, selected: Boolean) -> Unit
) {
    val contentSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val indicatorOffsetY = remember {
        mutableStateOf(0)
    }
    val layoutSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val listState = rememberLazyListState()
    val density = LocalDensity.current.density
    val animIndicatorOffsetY =
        animateIntAsState(targetValue = indicatorOffsetY.value)
    if (listState.isScrollInProgress) {
        if (listState.firstVisibleItemIndex <= selectedIndex.value && selectedIndex.value <=
            listState.firstVisibleItemIndex + listState.layoutInfo.viewportSize.height / contentSize.value.height
        ) {
            indicatorOffsetY.value =
                listState.layoutInfo.visibleItemsInfo.get(selectedIndex.value - listState.firstVisibleItemIndex).offset
        } else {
            if (indicatorOffsetY.value < listState.firstVisibleItemIndex) {
                indicatorOffsetY.value = -contentSize.value.height
            } else {
                indicatorOffsetY.value = listState.layoutInfo.viewportSize.height
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
                            0,
                            if (listState.isScrollInProgress) indicatorOffsetY.value else animIndicatorOffsetY.value
                        )
                    }, contentAlignment = indicatorAlignment
            ) {
                indicatorContent.invoke()
            }
        }

        val scope = rememberCoroutineScope()

        LazyColumn(
            Modifier
                .fillMaxSize()
                .onPlaced {
                    if (listState.firstVisibleItemIndex == 0 && selectedIndex.value == 0) {
                        indicatorOffsetY.value =
                            listState.layoutInfo.visibleItemsInfo.get(selectedIndex.value - listState.firstVisibleItemIndex).offset
                    }
                },
            verticalArrangement = verticalArrangement,
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
                            .height(((layoutSize.value.height / data.count()) / density).dp)
                            .wrapContentWidth()
                    } else {
                        modifier = modifier
                            .onPlaced {
                                contentSize.value = it.size
                            }
                            .height(((layoutSize.value.height / placeCount) / density).dp)
                            .wrapContentWidth()
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
                                    indicatorOffsetY.value =
                                        listState.layoutInfo.visibleItemsInfo.get(
                                            selectedIndex.value - listState.firstVisibleItemIndex
                                        ).offset
                                    if (autoScroll) {
                                        scope.launch {
                                            listState.animateScrollToItem(
                                                selectedIndex.value,
                                                -listState.layoutInfo.viewportSize.height / 2 + contentSize.value.height / 2
                                            )
                                            indicatorOffsetY.value =
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
                                indicatorOffsetY.value =
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
                            0,
                            if (listState.isScrollInProgress) indicatorOffsetY.value else animIndicatorOffsetY.value,
                        )
                    }, contentAlignment = indicatorAlignment
            ) {
                indicatorContent.invoke()
            }
        }


    }
}
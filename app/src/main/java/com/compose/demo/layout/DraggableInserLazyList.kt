package com.desaysv.hmicomponents.compose_lib.layout

import android.annotation.SuppressLint
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import com.compose.demo.widget.gridScrollbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun <T> DraggableInsertLazyColumn(
    modifier: Modifier = Modifier,
    data: List<T>,
    onExchangeEnd: (sourceIndex: Int, targetIndex: Int) -> Unit,
    itemContent: @Composable (item: T, index: Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    val draggingIndex = remember {
        mutableStateOf(-1)
    }
    val cancelIndex = remember {
        mutableStateOf(-1)
    }
    val changeIndex = remember {
        mutableStateOf(0)
    }//当前拖动到的位置的Item的index
    val itemHeight = remember {
        mutableStateOf(0)
    }
    val itemWidth = remember {
        mutableStateOf(0)
    }
    val layoutSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val itemOffsetX = remember {
        mutableStateOf(0)
    }
    val itemOffsetY = remember {
        mutableStateOf(0)
    }
    val animX =
        animateIntAsState(targetValue = itemOffsetX.value, TweenSpec(durationMillis = 800))
    val animY =
        animateIntAsState(targetValue = itemOffsetY.value, TweenSpec(durationMillis = 800))

    var autoScroll = remember {
        mutableStateOf(0)
    }

    val scrollstate = rememberLazyListState()

    LazyColumn(
        modifier = modifier
            .onPlaced {
                layoutSize.value = it.size
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(onDrag = { change, dragAmount ->
                    itemOffsetY.value += dragAmount.y.roundToInt()
                    itemOffsetX.value += dragAmount.x.roundToInt()
                    var index =
                        (itemOffsetY.value / itemHeight.value) + draggingIndex.value
                    if (changeIndex.value != index && index >= 0 && index <= data.size - 1) {
                        changeIndex.value = index
                        var ofx = itemOffsetX.value % itemWidth.value
                        var ofy = itemOffsetY.value % itemHeight.value
                        onExchangeEnd(draggingIndex.value, changeIndex.value)
                        draggingIndex.value = changeIndex.value
                        itemOffsetY.value = ofy
                        itemOffsetX.value = ofx
                    }
                    if (change.position.y <= itemHeight.value && autoScroll.value == -1) {
                        if (scrollstate.canScrollBackward) {
                            autoScroll.value = 0
                            scope.launch {
                                do {
                                    delay(10)
                                    scrollstate.scrollBy(-10f)
                                    itemOffsetY.value -= 10
                                    var index =
                                        (itemOffsetY.value / itemHeight.value) + draggingIndex.value

                                    if (index >= 0) {
                                        if (changeIndex.value != index && draggingIndex.value != -1) {
                                            changeIndex.value = index
                                            var ofx = itemOffsetX.value % itemWidth.value
                                            var ofy = itemOffsetY.value % itemHeight.value
                                            onExchangeEnd(draggingIndex.value, changeIndex.value)
                                            draggingIndex.value = changeIndex.value
                                            itemOffsetX.value = ofx
                                            itemOffsetY.value = ofy
                                        }
                                    }
                                } while (autoScroll.value == 0 && scrollstate.canScrollBackward)
                                autoScroll.value = -1
                            }
                        }
                    } else if (change.position.y >= layoutSize.value.height - itemHeight.value / 2 && autoScroll.value == -1) {
                        if (scrollstate.canScrollForward) {
                            autoScroll.value = 1
                            scope.launch {
                                do {
                                    delay(10)
                                    scrollstate.scrollBy(10f)
                                    itemOffsetY.value += 10
                                    var index =
                                        (itemOffsetY.value / itemHeight.value).toInt() + draggingIndex.value
                                    if (index <= data.size - 1) {
                                        if (changeIndex.value != index && draggingIndex.value != -1) {
                                            changeIndex.value = index
                                            var ofx = itemOffsetX.value % itemWidth.value
                                            var ofy = itemOffsetY.value % itemHeight.value
                                            onExchangeEnd(draggingIndex.value, changeIndex.value)
                                            draggingIndex.value = changeIndex.value
                                            itemOffsetX.value = ofx
                                            itemOffsetY.value = ofy
                                        }
                                    }
                                } while (autoScroll.value == 1 && scrollstate.canScrollForward)
                                autoScroll.value = -1
                            }
                        }
                    } else {
                        autoScroll.value = -1
                    }

                }, onDragStart = {
                    cancelIndex.value = -1
                    val offset =
                        scrollstate.firstVisibleItemScrollOffset + scrollstate.firstVisibleItemIndex * itemHeight.value
                    var index = ((it.y + offset) / itemHeight.value).toInt()
                    if (index < 0) {
                        index = 0
                    }
                    if (index > data.size - 1) {
                        index = data.size - 1
                    }
                    draggingIndex.value = index
                    itemOffsetX.value = 0
                    itemOffsetY.value = 0
                }, onDragEnd = {
                    cancelIndex.value = draggingIndex.value
                    draggingIndex.value = -1
                    changeIndex.value = -1
                    itemOffsetX.value = 0
                    itemOffsetY.value = 0
                    autoScroll.value = -1
                }, onDragCancel = {
                    cancelIndex.value = draggingIndex.value
                    draggingIndex.value = -1
                    changeIndex.value = -1
                    itemOffsetX.value = 0
                    itemOffsetY.value = 0
                    autoScroll.value = -1
                })
            },
        state = scrollstate
    ) {
        items(data.size, {
            if (it == 0 && scrollstate.canScrollForward) {
                0
            } else {
                data[it].hashCode()
            }
        }) { index ->
            val rememberIndex = remember {
                mutableStateOf(0)
            }
            rememberIndex.value = index
            var modifier = Modifier.onPlaced { }

            if (cancelIndex.value == index) {
                modifier = modifier
                    .zIndex(1f)
                    .offset {
                        IntOffset(animX.value, animY.value)
                    }
            } else if (index == draggingIndex.value) {
                modifier = modifier
                    .offset {
                        IntOffset(itemOffsetX.value, itemOffsetY.value)
                    }
                    .zIndex(1f)
            } else {
                modifier = modifier.animateItemPlacement()
            }

            Box(modifier = modifier.onPlaced {
                itemHeight.value = it.size.height
                itemWidth.value = it.size.width
            }, contentAlignment = Alignment.Center) {
                itemContent(data[index], index)
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> DraggableInsertLazyGrid(
    modifier: Modifier = Modifier,
    columCount: Int = 3,
    data: List<T>,
    onExchangeEnd: (sourceIndex: Int, targetIndex: Int) -> Unit,
    itemContent: @Composable (item: T, index: Int) -> Unit
) {

    val scope = rememberCoroutineScope()
    val layoutSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    var scrollstate = rememberLazyGridState()
    var layoutInfo: List<LazyGridItemInfo>? = null

    val draggingIndex = remember {
        mutableStateOf(-1)
    }
    val cancelIndex = remember {
        mutableStateOf(-1)
    }
    val changeIndex = remember {
        mutableStateOf(-1)
    }
    val itemHeight = remember {
        mutableStateOf(0)
    }
    val itemWidth = remember {
        mutableStateOf(0)
    }
    val itemOffsetX = remember {
        mutableStateOf(0)
    }
    val itemOffsetY = remember {
        mutableStateOf(0)
    }
    val animX =
        animateIntAsState(targetValue = itemOffsetX.value, TweenSpec(durationMillis = 800))
    val animY =
        animateIntAsState(targetValue = itemOffsetY.value, TweenSpec(durationMillis = 800))

    var autoScroll = remember {
        mutableStateOf(0)
    }

    LazyVerticalGrid(
        modifier = modifier.gridScrollbar(scrollstate, cols = 6)
            .onPlaced {
                layoutSize.value = it.size
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(onDrag = { change, dragAmount ->
                    itemOffsetY.value += dragAmount.y.roundToInt()
                    itemOffsetX.value += dragAmount.x.roundToInt()
                    var index =
                        (itemOffsetY.value / itemHeight.value) * columCount + (itemOffsetX.value / itemWidth.value) + draggingIndex.value
                    if (changeIndex.value != index && index >= 0 && index <= data.size - 1) {
                        changeIndex.value = index
                        var ofx = itemOffsetX.value % itemWidth.value
                        var ofy = itemOffsetY.value % itemHeight.value
                        onExchangeEnd(draggingIndex.value, changeIndex.value)
                        draggingIndex.value = changeIndex.value
                        itemOffsetY.value = ofy
                        itemOffsetX.value = ofx
                    }
                    if (change.position.y <= itemHeight.value && autoScroll.value == -1) {
                        if (scrollstate.canScrollBackward) {
                            autoScroll.value = 0
                            scope.launch {
                                do {
                                    delay(10)
                                    scrollstate.scrollBy(-10f)
                                    itemOffsetY.value -= 10
                                    var index =
                                        (itemOffsetY.value / itemHeight.value).toInt() * columCount + (itemOffsetX.value / itemWidth.value).toInt() + draggingIndex.value

                                    if (index >= 0) {
                                        if (changeIndex.value != index && draggingIndex.value != -1) {
                                            changeIndex.value = index
                                            var ofx = itemOffsetX.value % itemWidth.value
                                            var ofy = itemOffsetY.value % itemHeight.value
                                            onExchangeEnd(draggingIndex.value, changeIndex.value)
                                            draggingIndex.value = changeIndex.value
                                            itemOffsetX.value = ofx
                                            itemOffsetY.value = ofy
                                        }
                                    }
                                } while (autoScroll.value == 0 && scrollstate.canScrollBackward)
                                autoScroll.value = -1
                            }
                        }
                    } else if (change.position.y >= layoutSize.value.height - itemHeight.value / 2 && autoScroll.value == -1) {
                        if (scrollstate.canScrollForward) {
                            autoScroll.value = 1
                            scope.launch {
                                do {
                                    delay(10)
                                    scrollstate.scrollBy(10f)
                                    itemOffsetY.value += 10
                                    var index =
                                        (itemOffsetY.value / itemHeight.value).toInt() * columCount + (itemOffsetX.value / itemWidth.value).toInt() + draggingIndex.value
                                    if (index <= data.size - 1) {
                                        if (changeIndex.value != index && draggingIndex.value != -1) {
                                            changeIndex.value = index
                                            var ofx = itemOffsetX.value % itemWidth.value
                                            var ofy = itemOffsetY.value % itemHeight.value
                                            onExchangeEnd(draggingIndex.value, changeIndex.value)
                                            draggingIndex.value = changeIndex.value
                                            itemOffsetX.value = ofx
                                            itemOffsetY.value = ofy
                                        }
                                    }
                                } while (autoScroll.value == 1 && scrollstate.canScrollForward)
                                autoScroll.value = -1
                            }
                        }
                    } else {
                        autoScroll.value = -1
                    }

                }, onDragStart = {
                    cancelIndex.value = -1
                    var index = 0
                    layoutInfo = scrollstate.layoutInfo.visibleItemsInfo
                    layoutInfo?.forEach { item ->
                        if (it.y >= item.offset.y && it.y <= item.offset.y + item.size.height && it.x >= item.offset.x && it.x < item.offset.x + item.size.width) {
                            index = item.index
                        }
                    }
                    draggingIndex.value = index
                    itemOffsetX.value = 0
                    itemOffsetY.value = 0
                }, onDragEnd = {
                    cancelIndex.value = draggingIndex.value
                    draggingIndex.value = -1
                    changeIndex.value = -1
                    itemOffsetX.value = 0
                    itemOffsetY.value = 0
                    autoScroll.value = -1
                }, onDragCancel = {
                    cancelIndex.value = draggingIndex.value
                    draggingIndex.value = -1
                    changeIndex.value = -1
                    itemOffsetX.value = 0
                    itemOffsetY.value = 0
                    autoScroll.value = -1
                })
            },
        columns = GridCells.Fixed(6),
        state = scrollstate
    ) {
        items(data.size, {
            if (it == 0 && scrollstate.canScrollForward) {
                0
            } else {
                data[it].hashCode()
            }
        }) { index ->
            val rememberIndex = remember {
                mutableStateOf(0)
            }
            rememberIndex.value = index
            var modifier = Modifier.onPlaced { }

            if (cancelIndex.value == index) {
                modifier = modifier
                    .zIndex(1f)
                    .offset {
                        IntOffset(animX.value, animY.value)
                    }
            } else if (index == draggingIndex.value) {
                modifier = modifier
                    .offset {
                        IntOffset(itemOffsetX.value, itemOffsetY.value)
                    }
                    .zIndex(1f)
            } else {
                modifier = modifier.animateItemPlacement()
            }

            Box(modifier = modifier.onPlaced {
                itemHeight.value = it.size.height
                itemWidth.value = it.size.width
            }, contentAlignment = Alignment.Center) {
                itemContent(data[index], index)
            }
        }
    }
}


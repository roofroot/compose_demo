package com.desaysv.hmicomponents.compose_lib.layout

import android.annotation.SuppressLint
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Modifier.orderable(
    orderListState: OrderableListState,
    onExchangeEnd: (sourceIndex: Int, targetIndex: Int) -> Unit,
): Modifier {
    val scope = rememberCoroutineScope()

    val draggingIndex = orderListState.draggingIndex
    val cancelIndex = orderListState.cancelIndex
    val changeIndex = orderListState.changeIndex
    val itemHeight = orderListState.itemHeight
    val itemWidth = orderListState.itemWidth
    val layoutSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val itemOffsetX = orderListState.itemOffsetX
    val itemOffsetY = orderListState.itemOffsetY
    val scrollstate = orderListState.scrollState
    val data = orderListState.data
    var autoScroll = remember {
        mutableStateOf(0)
    }

    return this.then(
        Modifier
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
            })
}

@Composable
fun rememberOrderableListState(state: LazyListState, data: List<*>): OrderableListState {
    val orderListState = OrderableListState()
    orderListState.scrollState = state
    orderListState.data = data
    orderListState.itemHeight = remember {
        mutableStateOf(0)
    }
    orderListState.itemWidth = remember {
        mutableStateOf(0)
    }
    orderListState.layoutSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    orderListState.itemOffsetX = remember {
        mutableStateOf(0)
    }
    orderListState.itemOffsetY = remember {
        mutableStateOf(0)
    }
    orderListState.cancelIndex = remember {
        mutableStateOf(-1)
    }
    orderListState.draggingIndex = remember {
        mutableStateOf(-1)
    }
    orderListState.changeIndex = remember {
        mutableStateOf(-1)
    }//当前拖动到的位置的Item的index
    return orderListState
}


class OrderableListState {
    lateinit var scrollState: LazyListState
    lateinit var draggingIndex: MutableState<Int>
    lateinit var changeIndex: MutableState<Int>
    lateinit var cancelIndex: MutableState<Int>
    lateinit var itemHeight: MutableState<Int>
    lateinit var itemWidth: MutableState<Int>
    lateinit var layoutSize: MutableState<IntSize>
    lateinit var itemOffsetX: MutableState<Int>
    lateinit var itemOffsetY: MutableState<Int>
    lateinit var data: List<*>
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.OrderableItem(
    orderableState: OrderableListState,
    modifier: Modifier = Modifier,
    index: Int,
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit
) {
    val animX =
        animateIntAsState(
            targetValue = orderableState.itemOffsetX.value,
            TweenSpec(durationMillis = 800)
        )
    val animY =
        animateIntAsState(
            targetValue = orderableState.itemOffsetY.value,
            TweenSpec(durationMillis = 800)
        )
    val rememberIndex = remember {
        mutableStateOf(0)
    }
    rememberIndex.value = index
    var modifier = modifier.onPlaced {
        orderableState.itemHeight.value = it.size.height
        orderableState.itemWidth.value = it.size.width
    }

    if (orderableState.cancelIndex.value == index) {
        modifier = modifier
            .zIndex(1f)
            .offset {
                IntOffset(animX.value, animY.value)
            }
    } else if (index == orderableState.draggingIndex.value) {
        modifier = modifier
            .offset {
                IntOffset(orderableState.itemOffsetX.value, orderableState.itemOffsetY.value)
            }
            .zIndex(1f)
    } else {
        modifier = modifier.animateItemPlacement()
    }
    Box(modifier = modifier) {
        content(index == orderableState.draggingIndex.value)
    }
}


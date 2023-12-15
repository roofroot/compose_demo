package com.desaysv.hmicomponents.compose_lib.layout

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.compose.demo.R
import com.compose.demo.ui.page.MyListData
import com.compose.demo.ui.page.getRandomColor
import com.compose.demo.util.LogUtil
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun <T> DraggableInsertLazyColumn(
    modifier: Modifier = Modifier,
    data: List<T>,
    onExchangeEnd: (sourceIndex: Int, targetIndex: Int) -> Unit,
    hoverItemContent: @Composable (item: T, index: Int) -> Unit,
    itemContent: @Composable (item: T, index: Int, modifier: Modifier) -> Unit
) {
    val scope = rememberCoroutineScope()

    val moveItemVisible = remember {
        mutableStateOf(false)
    }
    val changeIndex = remember {
        mutableStateOf(0)
    }//当前拖动到的位置的Item的index
    val moveIndex = remember {
        mutableStateOf(0)
    }//长按后被拖动的Item的index
    val itemHeight = remember {
        mutableStateOf(0)
    }
    val itemWidth = remember {
        mutableStateOf(0)
    }
    val layoutSize = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val offsetY = remember {
        mutableStateOf(0f)
    }
    val offsetX = remember {
        mutableStateOf(0f)
    }

    val scrollstate = rememberLazyListState()
    Box(
        modifier
            .onPlaced {
                layoutSize.value = it.size
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(onDragStart = {
                    offsetY.value = it.y - itemHeight.value / 2
                    offsetX.value = 5f
                    val offset =
                        scrollstate.firstVisibleItemScrollOffset + scrollstate.firstVisibleItemIndex * itemHeight.value
                    var index = ((it.y + offset) / itemHeight.value).toInt()
                    if (index < 0) {
                        index = 0
                    }
                    if (index > data.size - 1) {
                        index = data.size - 1
                    }
                    moveIndex.value = index
                    changeIndex.value = moveIndex.value
                    moveItemVisible.value = true
                }, onDragCancel = {
                    offsetX.value = 0f
                    offsetY.value = 0f
                    moveItemVisible.value = false
                }, onDragEnd = {
                    moveItemVisible.value = false
                    onExchangeEnd.invoke(moveIndex.value, changeIndex.value)
                    LogUtil.I(
                        changeIndex.value.toString() + "," + moveIndex.value.toString()
                    )
                }) { change, dragAmount ->

                    val offset =
                        scrollstate.firstVisibleItemScrollOffset + scrollstate.firstVisibleItemIndex * itemHeight.value
                    offsetY.value += dragAmount.y
                    offsetX.value += dragAmount.x
                    var index = ((offsetY.value + offset) / itemHeight.value).toInt()
                    if (index < 0) {
                        index = 0
                    }
                    if (index > data.size - 1) {
                        index = data.size - 1
                    }
                    changeIndex.value = index
                    LogUtil.I(
                        changeIndex.value.toString() + "," + moveIndex.value.toString() + "," + itemHeight.value
                    )

                    if (offsetY.value <= itemHeight.value && dragAmount.y < 0) {
                        if (scrollstate.canScrollBackward) {
                            scope.launch {
                                scrollstate.scrollBy(data.size.toFloat() / 2 * dragAmount.y)
                            }
                        }
                    }
                    if (offsetY.value >= layoutSize.value.height - itemHeight.value && dragAmount.y > 0) {
                        if (scrollstate.canScrollForward) {
                            scope.launch {
                                scrollstate.scrollBy(data.size.toFloat() / 2 * dragAmount.y)
                            }
                        }
                    }
                }
            }) {
        LazyColumn(
            state = scrollstate
        ) {
            itemsIndexed(data) { index, itemData ->
                var modifier = Modifier.wrapContentHeight()
                if (itemHeight.value != 0) {
                    modifier = Modifier.height((itemHeight.value / LocalDensity.current.density).dp)
                }
                var offsetx = remember {
                    mutableStateOf(0)
                }
                var offsety = remember {
                    mutableStateOf(0)
                }
                val animOffsetY = animateIntAsState(targetValue = offsety.value)
                var modifierOffset = Modifier.offset {
                    IntOffset(0, animOffsetY.value)
                }

                modifier = modifier.then(modifierOffset)


                if (moveItemVisible.value) {

                    if (changeIndex.value >= moveIndex.value) {
                        if (index < moveIndex.value) {
                            offsetx.value = 0
                            offsety.value = 0
                        } else if (index <= changeIndex.value) {
                            offsety.value = -itemHeight.value
                        } else {
                            offsety.value = 0
                        }
                    } else {
                        if (index > moveIndex.value) {
                            offsetx.value = 0
                            offsety.value = 0
                        } else if (index >= changeIndex.value) {
                            offsety.value = itemHeight.value
                        } else {
                            offsety.value = 0
                        }
                    }
                } else {
                    offsety.value = 0
                }


                Box(

                ) {

                    itemContent(itemData,
                        index,
                        modifier
                            .alpha(if (moveIndex.value != index || !moveItemVisible.value) 1f else 0f)
                            .onPlaced {
                                itemHeight.value = it.size.height
                                itemWidth.value = it.size.width
                            })


                }
            }
        }
        AnimatedVisibility(visible = moveItemVisible.value, exit = ExitTransition.None) {
            Box(Modifier.offset {
                IntOffset(
                    offsetX.value.roundToInt() + 5, offsetY.value.roundToInt() + 5
                )
            }) {
                hoverItemContent(data.get(moveIndex.value), moveIndex.value)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> DraggableInsertLazyGrid(
    modifier: Modifier = Modifier,
    columCount: Int = 3,
    data: MutableList<T>,
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
        modifier = modifier
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
                        data.add(changeIndex.value, data.removeAt(draggingIndex.value))
                        draggingIndex.value = changeIndex.value
                        itemOffsetY.value = ofy
                        itemOffsetX.value = ofx
                    }
                    if (change.position.y <= itemHeight.value) {
                        Log.e("aaaaaaaaaaa", dragAmount.y.toString())
                        var index = draggingIndex.value - columCount
                        if (index >= 0 && autoScroll.value == -1) {
                            autoScroll.value = 0
                            scope.launch {
                                Log.e("aaaaaaaaaaa", dragAmount.y.toString())
                                var totalMove = 0
                                do {
                                    delay(10)
                                    scrollstate.scrollBy(-10f)
                                    itemOffsetY.value -= 10
                                    var index =
                                        (itemOffsetY.value / itemHeight.value).toInt() * columCount + (itemOffsetX.value / itemWidth.value).toInt() + draggingIndex.value
                                    Log.e(
                                        "aaaaaaaaaa:",
                                        (itemOffsetY.value / itemHeight.value).toString()
                                    )
                                    if (index >= 0) {
                                        if (changeIndex.value != index && draggingIndex.value != -1) {
                                            changeIndex.value = index
                                            val temp = data.get(draggingIndex.value)
                                            var ofx = itemOffsetX.value % itemWidth.value
                                            var ofy = itemOffsetY.value % itemHeight.value
                                            data.removeAt(draggingIndex.value)
                                            data.add(changeIndex.value, temp)
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
                        var index = draggingIndex.value + columCount
                        if (index <= data.size - 1) {
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
                                            val temp = data.get(draggingIndex.value)
                                            var ofx = itemOffsetX.value % itemWidth.value
                                            var ofy = itemOffsetY.value % itemHeight.value
                                            data.removeAt(draggingIndex.value)
                                            data.add(changeIndex.value, temp)
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
            if (it == 0) {
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


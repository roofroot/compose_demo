package com.compose.demo.layout

import android.annotation.SuppressLint
import android.graphics.Paint.Align
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> DraggableInsertLazyColumn(
    modifier: Modifier = Modifier,
    data: MutableList<T>,
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
//                    offsetY.value = -1000f

                    moveItemVisible.value = false


                    Log.e(
                        "ccccccccccccc",
                        changeIndex.value.toString() + "," + moveIndex.value.toString()
                    )
                    val temp = data.get(moveIndex.value)
                    data.removeAt(moveIndex.value)
                    data.add(changeIndex.value, temp)
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
                    Log.e(
                        "ccccccccccccc",
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
//                if (moveItemVisible.value) {
                modifier = modifier.then(modifierOffset)
//                }

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

@Composable
fun <T> DraggableInsertLazyGrid(
    modifier: Modifier = Modifier,
    columCount: Int = 3,
    data: MutableList<T>,
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
    val contentWidth = remember {
        mutableStateOf(0)
    }
    val contentHeight = remember {
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

    val scrollstate = rememberLazyGridState()
    Box(
        Modifier
            .onPlaced {
                layoutSize.value = it.size
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(onDragStart = {
                    offsetY.value = it.y - contentHeight.value / 2
                    offsetX.value = it.x - contentWidth.value / 2
                    val offset =
                        scrollstate.firstVisibleItemScrollOffset + scrollstate.firstVisibleItemIndex * itemHeight.value / columCount
                    var index =
                        ((it.y + offset) / itemHeight.value).toInt() * columCount + (it.x / itemWidth.value).toInt()
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
                    moveItemVisible.value = false
                }, onDragEnd = {
                    offsetY.value = -1000f
                    moveItemVisible.value = false
                    Log.e(
                        "index", changeIndex.value.toString() + "," + moveIndex.value.toString()
                    )
                    val temp = data.get(moveIndex.value)
                    data.removeAt(moveIndex.value)
                    data.add(changeIndex.value, temp)

                }) { change, dragAmount ->

                    val offset =
                        scrollstate.firstVisibleItemScrollOffset + scrollstate.firstVisibleItemIndex * itemHeight.value / columCount
                    offsetY.value += dragAmount.y
                    offsetX.value += dragAmount.x
                    var index =
                        ((offsetY.value + offset) / itemHeight.value).toInt() * columCount + (offsetX.value / itemWidth.value).toInt()
                    if (index < 0) {
                        index = 0
                    }
                    if (index > data.size - 1) {
                        index = data.size - 1
                    }
                    changeIndex.value = index
                    Log.e(
                        "index",
                        changeIndex.value.toString() + "," + moveIndex.value.toString() + "," + itemHeight.value
                    )

                    if (offsetY.value <= itemHeight.value * 3) {
                        if (scrollstate.canScrollBackward) {
                            scope.launch {
                                scrollstate.scrollBy(-data.size.toFloat() / 2)
                            }
                        }
                    }
                    if (offsetY.value >= layoutSize.value.height - itemHeight.value * 3) {
                        if (scrollstate.canScrollForward) {
                            scope.launch {
                                scrollstate.scrollBy(data.size.toFloat() / 2)
                            }
                        }
                    }
                }
            }) {

        LazyVerticalGrid(
            modifier = modifier, columns = GridCells.Fixed(columCount), state = scrollstate
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
                val animOffsetX = animateIntAsState(targetValue = offsetx.value)

                var modifierOffset = Modifier.offset {
                    IntOffset(animOffsetX.value, animOffsetY.value)
                }

                if (changeIndex.value >= moveIndex.value) {
                    if (index < moveIndex.value) {
                        offsetx.value = 0
                        offsety.value = 0
                    } else if (index <= changeIndex.value && moveItemVisible.value) {
                        if (index % columCount == 0) {
                            offsety.value = -itemHeight.value
                            offsetx.value = itemWidth.value * (columCount - 1)
                        } else {
                            offsety.value = 0
                            offsetx.value = -itemWidth.value
                        }
                    } else {
                        offsety.value = 0
                        offsetx.value = 0
                    }

                } else {
                    if (index > moveIndex.value) {
                        offsety.value = 0
                        offsetx.value = 0
                    } else if (index >= changeIndex.value && moveItemVisible.value) {
                        if (index % columCount == columCount - 1) {
                            offsety.value = itemHeight.value
                            offsetx.value = -itemWidth.value * (columCount - 1)
                        } else {
                            offsety.value = 0
                            offsetx.value = itemWidth.value
                        }
                    } else {
                        offsety.value = 0
                        offsetx.value = 0
                    }
                }
//                if (moveItemVisible.value) {
                modifier = modifier.then(modifierOffset)
//                } else {
//                    modifier.offset { IntOffset(0, 0) }
//                }

                Box(
                    modifier.alpha(if (moveIndex.value != index || !moveItemVisible.value) 1f else 0f)
                ) {

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .onPlaced {
                                itemHeight.value = it.size.height
                                itemWidth.value = it.size.width
                            }, contentAlignment = Alignment.Center
                    ) {
                        itemContent(itemData, index, Modifier.onPlaced {
                            contentHeight.value = it.size.height
                            contentWidth.value = it.size.width
                        })
                    }


                }
            }
        }
        AnimatedVisibility(visible = moveItemVisible.value, exit = ExitTransition.None) {
            Box(
                Modifier
                    .width((itemWidth.value / LocalDensity.current.density).dp)
                    .offset {
                        IntOffset(
                            offsetX.value.roundToInt() + 5, offsetY.value.roundToInt() + 5
                        )
                    }) {
                hoverItemContent(data.get(moveIndex.value), moveIndex.value)
            }
        }
    }
}


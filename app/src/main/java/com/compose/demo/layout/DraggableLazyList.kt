package com.compose.demo.layout

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T> DraggableLazyColumn(
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
        Modifier
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
                    offsetX.value = 0f
                    offsetY.value = 0f
                    moveItemVisible.value = false
                    Log.e(
                        "ccccccccccccc",
                        changeIndex.value.toString() + "," + moveIndex.value.toString()
                    )
                    val temp = data.get(moveIndex.value)
                    data[moveIndex.value] = data.get(changeIndex.value)
                    data[changeIndex.value] = temp

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
                Box(
                    modifier
                ) {
                    AnimatedVisibility(
                        visible = (moveIndex.value != index && changeIndex.value != index) || !moveItemVisible.value
                    ) {
                        itemContent(itemData, index, Modifier.onPlaced {
                            itemHeight.value = it.size.height
                            itemWidth.value = it.size.width
                        })
                    }
                    AnimatedVisibility(visible = changeIndex.value == index && moveItemVisible.value) {
                        Box(Modifier.alpha(0.5f)) {
                            itemContent(data.get(moveIndex.value), moveIndex.value, Modifier)
                        }
                    }
                    AnimatedVisibility(visible = moveIndex.value == index && changeIndex.value != index && moveItemVisible.value) {
                        Box(Modifier.alpha(0.5f)) {
                            itemContent(data.get(changeIndex.value), changeIndex.value, Modifier)
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = moveItemVisible.value) {
            Box(Modifier
                .offset {
                    IntOffset(
                        offsetX.value.roundToInt() + 5,
                        offsetY.value.roundToInt() + 5
                    )
                }
            ) {
                hoverItemContent(data.get(moveIndex.value), moveIndex.value)
            }
        }
    }
}

@Composable
fun <T> DraggableLazyGrid(
    modifier: Modifier,
    rowCount: Int = 3,
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
                        scrollstate.firstVisibleItemScrollOffset + scrollstate.firstVisibleItemIndex * itemHeight.value / rowCount
                    var index =
                        ((it.y + offset) / itemHeight.value).toInt() * rowCount + (it.x / itemWidth.value).toInt()
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
                    moveItemVisible.value = false
                    Log.e(
                        "index",
                        changeIndex.value.toString() + "," + moveIndex.value.toString()
                    )
                    val temp = data.get(moveIndex.value)
                    data[moveIndex.value] = data.get(changeIndex.value)
                    data[changeIndex.value] = temp

                }) { change, dragAmount ->

                    val offset =
                        scrollstate.firstVisibleItemScrollOffset + scrollstate.firstVisibleItemIndex * itemHeight.value / rowCount
                    offsetY.value += dragAmount.y
                    offsetX.value += dragAmount.x
                    var index =
                        ((offsetY.value + offset) / itemHeight.value).toInt() * rowCount + (offsetX.value / itemWidth.value).toInt()
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
            modifier = modifier,
            columns = GridCells.Fixed(rowCount),
            state = scrollstate
        ) {
            itemsIndexed(data) { index, itemData ->
                var modifier = Modifier.wrapContentHeight()
                if (itemHeight.value != 0) {
                    modifier = Modifier.height((itemHeight.value / LocalDensity.current.density).dp)
                }
                Box(
                    modifier
                ) {
                    AnimatedVisibility(
                        visible = (moveIndex.value != index && changeIndex.value != index) || !moveItemVisible.value
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
                    AnimatedVisibility(visible = changeIndex.value == index && moveItemVisible.value) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(), contentAlignment = Alignment.Center
                        ) {
                            Box(Modifier.alpha(0.5f)) {
                                itemContent(data.get(moveIndex.value), moveIndex.value, Modifier)
                            }
                        }
                    }
                    AnimatedVisibility(visible = moveIndex.value == index && changeIndex.value != index && moveItemVisible.value) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(), contentAlignment = Alignment.Center
                        ) {
                            Box(Modifier.alpha(0.5f)) {
                                itemContent(
                                    data.get(changeIndex.value),
                                    changeIndex.value,
                                    Modifier
                                )
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = moveItemVisible.value, exit = ExitTransition.None) {
            Box(Modifier
                .offset {
                    IntOffset(
                        offsetX.value.roundToInt() + 5,
                        offsetY.value.roundToInt() + 5
                    )
                }
            ) {
                hoverItemContent(data.get(moveIndex.value), moveIndex.value)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> DraggableLazyStaggeredGrid(
    modifier: Modifier,
    rowCount: Int = 3,
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

    val scrollstate = rememberLazyStaggeredGridState()
    var layoutInfo: List<LazyStaggeredGridItemInfo>? = null

    Box(
        Modifier
            .onPlaced {
                layoutSize.value = it.size
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(onDragStart = {
                    offsetY.value = it.y - contentHeight.value / 2
                    offsetX.value = it.x - contentWidth.value / 2
                    var index = 0
                    layoutInfo = scrollstate.layoutInfo.visibleItemsInfo
                    layoutInfo?.forEach { item ->
                        if (it.y >= item.offset.y && it.y <= item.offset.y + item.size.height && it.x >= item.offset.x && it.x < item.offset.x + item.size.width) {
                            index = item.index
                        }
                    }
                    moveIndex.value = index
                    changeIndex.value = moveIndex.value
                    moveItemVisible.value = true
                }, onDragCancel = {
                    moveItemVisible.value = false
                }, onDragEnd = {
                    moveItemVisible.value = false
                    Log.e(
                        "index",
                        changeIndex.value.toString() + "," + moveIndex.value.toString()
                    )
                    val temp = data.get(moveIndex.value)
                    data[moveIndex.value] = data.get(changeIndex.value)
                    data[changeIndex.value] = temp

                }) { change, dragAmount ->

                    offsetY.value += dragAmount.y
                    offsetX.value += dragAmount.x
                    val posY = offsetY.value + contentHeight.value / 2
                    val posX = offsetX.value + contentWidth.value / 2
                    var index = 0
                    layoutInfo?.forEach { item ->
                        if (posY >= item.offset.y && posY <= item.offset.y + item.size.height && posX >= item.offset.x && posX < item.offset.x + item.size.width) {
                            index = item.index
                            return@forEach
                        }
                    }
                    changeIndex.value = index
                    Log.e(
                        "index",
                        changeIndex.value.toString() + "," + moveIndex.value.toString() + "," + itemHeight.value
                    )


                    if (offsetY.value < 300 && dragAmount.y < 0) {
                        if (scrollstate.canScrollBackward) {
                            scope.launch {
                                scrollstate.scrollBy(data.size.toFloat() / rowCount * dragAmount.y)
                                layoutInfo = scrollstate.layoutInfo.visibleItemsInfo
                            }
                        }
                    }
                    if (offsetY.value > layoutSize.value.height - 300 && dragAmount.y > 0) {
                        if (scrollstate.canScrollForward) {
                            scope.launch {
                                scrollstate.scrollBy(data.size.toFloat() / rowCount * dragAmount.y)
                                layoutInfo = scrollstate.layoutInfo.visibleItemsInfo
                            }
                        }
                    }
                }
            }) {
        LazyVerticalStaggeredGrid(
            modifier = modifier,
            columns = StaggeredGridCells.Fixed(rowCount),
            state = scrollstate
        ) {
            itemsIndexed(data) { index, itemData ->

                var showIndex = remember {
                    mutableStateOf(0)
                }
                var alpha = remember {
                    mutableStateOf(1f)
                }
                var animAlpha = animateFloatAsState(targetValue = alpha.value)

                if ((moveIndex.value != index && changeIndex.value != index) || !moveItemVisible.value) {
                    showIndex.value = index
                    alpha.value = 1f
                } else if (changeIndex.value == index && moveItemVisible.value) {
                    showIndex.value = moveIndex.value
                    alpha.value = 0.5f
                } else if (moveIndex.value == index && changeIndex.value != index && moveItemVisible.value) {

                    showIndex.value = changeIndex.value
                    alpha.value = 0.5f
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .onPlaced {
                            itemHeight.value = it.size.height
                            itemWidth.value = it.size.width
                        }
                        .alpha(animAlpha.value), contentAlignment = Alignment.Center
                ) {
                    itemContent(
                        data.get(index = showIndex.value),
                        showIndex.value,
                        Modifier.onPlaced {
                            contentHeight.value = it.size.height
                            contentWidth.value = it.size.width
                        })
                }

            }
        }
        AnimatedVisibility(visible = moveItemVisible.value, exit = ExitTransition.None) {
            Box(Modifier
                .offset {
                    IntOffset(
                        offsetX.value.roundToInt() + 5,
                        offsetY.value.roundToInt() + 5
                    )
                }
            ) {
                hoverItemContent(data.get(moveIndex.value), moveIndex.value)
            }
        }
    }
}
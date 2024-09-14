package com.compose.demo.widget

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPickerCommon(
    data: List<T>,
    selectIndex: Int,
    visibleCount: Int,
    modifier: Modifier = Modifier,
    onSelect: (index: Int, item: T) -> Unit,
    textContent: @Composable (item: T) -> String
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        BoxWithConstraints(modifier = modifier, propagateMinConstraints = true) {
            val density = LocalDensity.current
            val size = data.size
            val count = size
            val pickerHeight = maxHeight
            val pickerHeightPx = density.run { pickerHeight.toPx() }
            val pickerCenterLinePx = pickerHeightPx / 2
            val itemHeight = pickerHeight / visibleCount
            val itemHeightPx = density.run { itemHeight.toPx() }
            val listState = rememberLazyListState(
                initialFirstVisibleItemIndex = selectIndex,
                initialFirstVisibleItemScrollOffset = ((itemHeightPx - pickerHeightPx) / 2).roundToInt(),
            )
            LaunchedEffect(key1 = selectIndex) {
                listState.animateScrollToItem(selectIndex)
            }
            LazyColumn(
                modifier = Modifier,
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(listState),
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                    )
                }
                items(count) { index ->

                    var state = remember {
                        mutableStateOf(0)
                    }
                    var scale = getScale(
                        data = data,
                        index = index,
                        state = state,
                        pickerCenterLinePx = pickerCenterLinePx,
                        itemHeightPx = itemHeightPx,
                        listState = listState,
                        onSelect = onSelect
                    )
                    Row {
                        ItemMarkLine(
                            itemHeight = itemHeight,
                            state = state,
                            index = index,
                            size = data.size
                        )
                        ItemView(
                            data = data,
                            itemHeight = itemHeight,
                            index = index,
                            scale = scale,
                            textContent
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> ItemView(
    data: List<T>,
    itemHeight: Dp,
    index: Int,
    scale: Float,
    textContent:@Composable (item: T) -> String
) {
    val animFontSize =
        animateIntAsState(targetValue = (28 + 10 * scale).roundToInt())
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = textContent(data[index]),
            fontSize = animFontSize.value.sp,
            color = Color.Gray
        )
        Text(
            modifier = Modifier.alpha(scale),
            text = textContent(data[index]),
            fontSize = animFontSize.value.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun <T> getScale(
    data: List<T>,
    index: Int,
    state: MutableState<Int>,
    pickerCenterLinePx: Float,
    itemHeightPx: Float,
    listState: LazyListState,
    onSelect: (index: Int, item: T) -> Unit
): Float {
    val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
    val item = layoutInfo.visibleItemsInfo.find { it.index - 1 == index }
    var scale = 1f
    val startToScroll = remember {
        mutableStateOf(false)
    }
    if (item != null) {
        val itemCenterY = item.offset + item.size / 2
        scale = if (itemCenterY < pickerCenterLinePx) {
            1 - (pickerCenterLinePx - itemCenterY) / itemHeightPx
        } else {
            1 - (itemCenterY - pickerCenterLinePx) / itemHeightPx
        }
        when {
            item.offset < pickerCenterLinePx && item.offset + item.size > pickerCenterLinePx -> {
                if (listState.isScrollInProgress) {
                    startToScroll.value = true
                } else {
                    if (startToScroll.value) {
                        onSelect(index, data[index])
                        startToScroll.value = false
                    }
                }
                state.value = 0
            }

            (item.offset + item.size) < pickerCenterLinePx -> {
                state.value = -1
            }

            item.offset > pickerCenterLinePx -> {
                state.value = 1
            }
        }
    }
    return scale
}
package com.compose.demo.widget

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Modifier.columnScrollbar(
    state: LazyListState,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0f,
    scrollBarState: ScrollBarState,
    scrollBarHeight: Dp = 115.dp,
    fadeOutEdge: Boolean = false,
    fadeEdgeTop: Dp = 10.dp,
    fadeEdgeBottom: Dp = 30.dp,
    spaceHeight: Dp = 0.dp
): Modifier {
    LaunchedEffect(Unit) {
        val firstItem = state.firstVisibleItemIndex
        val firstItemOffset = state.firstVisibleItemScrollOffset
        if (state.layoutInfo.totalItemsCount > 1) {
            state.scrollToItem(index = state.layoutInfo.totalItemsCount - 1)
            state.scrollToItem(0)
            state.scrollToItem(firstItem, firstItemOffset)
        }
    }
    scrollBarState.alpha.value =
        if (state.isScrollInProgress && (state.canScrollForward || state.canScrollBackward) || scrollBarState.isDragging.value) {
            visibleAlpha
        } else {
            hiddenAlpha
        }

    return alpha(if (fadeOutEdge) 0.99f else 1f).drawWithContent {
        drawContent()
        var totalHeight = 0f
        var offsetOne = 0f
        var offsetIndex = 0
        var offsetHeight = 0f
        scrollBarState.childRectMap.toList().forEach {
            val rect = it.second
            if (it.first >= offsetIndex && rect.top < 0
                && it.first < state.firstVisibleItemIndex + state.layoutInfo.visibleItemsInfo.size
            ) {
                offsetOne = -rect.top
                offsetIndex = it.first
            }
            totalHeight += rect.height + (if (totalHeight != 0f) spaceHeight.toPx() else 0f)
        }
        scrollBarState.childRectMap.forEach { (i, rect) ->
            if (i < offsetIndex) {
                offsetHeight += rect.height + (if (offsetHeight != 0f) spaceHeight.toPx() else 0f)
            }
        }
        offsetHeight += offsetOne

        val a = size.height - scrollBarHeight.toPx()

        var x = a / (totalHeight - size.height)

        val offsetY =
            if (offsetHeight > 0) (offsetHeight * x) else 0f
        if (fadeOutEdge) {
            val top = fadeEdgeTop.toPx() / (state.layoutInfo.viewportSize.height)
            val bottom = fadeEdgeBottom.toPx() / (state.layoutInfo.viewportSize.height)
            //如果隐藏边缘属性设置为ture并且列表在滚动中，实现边缘渐隐效果
            drawRect(
                Brush.verticalGradient(
                    Pair(0.0f, if (state.canScrollBackward) Color.Transparent else Color.Black),
                    Pair(top, Color.Black),
                    Pair(1f - bottom, Color.Black),
                    Pair(1f, if (state.canScrollForward) Color.Transparent else Color.Black),
                    startY = 0f,
                    endY = state.layoutInfo.viewportSize.height.toFloat()
                ), blendMode = BlendMode.DstIn
            )
        }
        if (scrollBarState.isDragging.value == false) {
            scrollBarState.offsetY.value = offsetY
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.columnScrollbarView(
    state: LazyListState,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 4000,
    scrollBarState: ScrollBarState,
    scrollBarHeight: Dp = 115.dp,
    spaceHeight: Dp = 0.dp
): Modifier {

    val animationDurationMs =
        if (state.isScrollInProgress && (state.canScrollForward || state.canScrollBackward)) {
            fadeInAnimationDurationMs
        } else {
            fadeOutAnimationDurationMs
        }
    val animationDelayMs =
        if (state.isScrollInProgress && (state.canScrollForward || state.canScrollBackward)) {
            0
        } else {
            fadeOutAnimationDelayMs
        }
    val density = LocalDensity.current.density
    val alpha by
    animateFloatAsState(
        targetValue = scrollBarState.alpha.value,
        animationSpec =
        tween(delayMillis = animationDelayMs, durationMillis = animationDurationMs)
    )

    val scope = rememberCoroutineScope()
    val draggableState = rememberDraggableState { delta ->
        var totalHeight = 0f
        var offsetOne = 0f
        var offsetIndex = 0
        var offsetHeight = 0f
        scrollBarState.childRectMap
            .toList()
            .forEach {
                val rect = it.second
                if (it.first >= offsetIndex && rect.top < 0
                    && it.first < state.firstVisibleItemIndex + state.layoutInfo.visibleItemsInfo.size
                ) {
                    offsetOne = -rect.top
                    offsetIndex = it.first
                }
                totalHeight += rect.height + (if (totalHeight != 0f) spaceHeight.value * density else 0f)
            }
        scrollBarState.childRectMap.forEach { (i, rect) ->
            if (i < offsetIndex) {
                offsetHeight += rect.height + (if (offsetHeight != 0f) spaceHeight.value * density else 0f)
            }
        }
        offsetHeight += offsetOne + spaceHeight.value * density

        val a = state.layoutInfo.viewportSize.height - scrollBarHeight.value * density

        var x = a / (totalHeight - state.layoutInfo.viewportSize.height)

        if (state.canScrollForward && delta > 0 || state.canScrollBackward && delta < 0) {
            if (scrollBarState.offsetY.value + delta < 0) {
                scrollBarState.offsetY.value = 0f
            } else if (scrollBarState.offsetY.value + delta > a) {
                scrollBarState.offsetY.value = a
            } else {
                scrollBarState.offsetY.value += delta
            }
            scope.launch {
                state.scrollBy(delta / x)
            }
        }
    }
    return offset(0.dp, (scrollBarState.offsetY.value / density).dp)
        .pointerInput(Unit) {
            detectTapGestures(onPress = {
                scrollBarState.isDragging.value = true
            }, onTap = {
                scrollBarState.isDragging.value = false
            })
        }
        .draggable(draggableState, orientation = Orientation.Vertical, onDragStarted = {
            scrollBarState.isDragging.value = true
        }, onDragStopped = {
            scrollBarState.isDragging.value = false
        })
        .alpha(alpha)
}

@Composable
fun Modifier.itemScrollBar(index: Int, scrollBarState: ScrollBarState): Modifier {
    return onPlaced {
        scrollBarState.childRectMap.put(index, it.boundsInParent())
    }
}

@Composable
fun getScrollBarState(): ScrollBarState {
    val scrollBarState = remember {
        mutableStateOf(ScrollBarState())
    }
    scrollBarState.value.childRectMap = remember {
        mutableStateMapOf()
    }
    scrollBarState.value.offsetY = remember {
        mutableStateOf(0f)
    }
    scrollBarState.value.isDragging = remember {
        mutableStateOf(false)
    }
    scrollBarState.value.alpha = remember {
        mutableStateOf(0f)
    }
    return scrollBarState.value
}

class ScrollBarState {
    lateinit var childRectMap: MutableMap<Int, Rect>
    lateinit var offsetY: MutableState<Float>
    lateinit var isDragging: MutableState<Boolean>
    lateinit var alpha: MutableState<Float>
}

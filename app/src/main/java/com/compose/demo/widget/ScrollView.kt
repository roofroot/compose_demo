package com.desaysv.hmi.component.container

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ScrollView(
    modifier: Modifier,
    scrollBarWidth: Dp = 5.dp,
    scrollBarHeight: Dp = 115.dp,
    scrollBarColor: Color = Color.Black,
    content: @Composable (state: ScrollState) -> Unit
) {
    val scrollState = rememberScrollState()
    val scrollViewHeight = remember {
        mutableStateOf(0)
    }
    val scrollViewWidth = remember {
        mutableStateOf(0)
    }
    Box(modifier.onPlaced {
        scrollViewHeight.value = it.size.height
        scrollViewWidth.value = it.size.width
    }) {
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            content(scrollState)
        }
        AnimatedVisibility(
            visible = scrollState.isScrollInProgress, enter = EnterTransition.None, exit = fadeOut(
                animationSpec =
                tween(delayMillis = 4000, durationMillis = 500)
            )
        ) {
            val a = scrollViewHeight.value - scrollBarHeight.value * LocalDensity.current.density
            //这一行的好意思是 视口的高度减去进度条的高度
            var x = a / (scrollState.maxValue)
            //用这个值除以控件总高度减去视口的高度， 就可以得到我们窗口实际滚动的距离与进度条实际可以滚动的范围的一个比例
            val offsetY =
                if (scrollState.value > 0) ((scrollState.value * x) / LocalDensity.current.density).roundToInt().dp else 0.dp
            //将这个比例值乘以实际滚动的距离，就能得出进度条应该显示的位置了
            Box(
                modifier = Modifier
                    .offset(
                        (scrollViewWidth.value / LocalDensity.current.density).dp - scrollBarWidth,
                        offsetY
                    )
                    .width(scrollBarWidth)
                    .height(scrollBarHeight)
                    .background(
                        scrollBarColor,
                        shape = RoundedCornerShape(CornerSize(scrollBarWidth / 2))
                    )
            ) {

            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.scrollbar(
    state: ScrollState,
    knobCornerRadius: Dp = 4.dp,
    scrollMarginRight: Dp = 20.dp,
    knobColor: Color = Color.Black,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0f,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 4000,
    scrollBarWidth: Dp = 5.dp,
    scrollBarHeight: Dp = 115.dp,
    showScrollTrack: Boolean = false,
    bgColor: Color = Color.Black,
    scrollTrackWidth: Dp = 1.dp,
    fadeOutEdge: Boolean = false,
    fadeEdgeTop: Dp = 10.dp,
    fadeEdgeBottom: Dp = 30.dp
): Modifier {

    val targetAlpha = remember {
        mutableStateOf(hiddenAlpha)
    }
    val animationDurationMs =
        if (targetAlpha.value == visibleAlpha) {
            fadeInAnimationDurationMs
        } else {
            fadeOutAnimationDurationMs
        }
    val animationDelayMs =
        if (targetAlpha.value == visibleAlpha) {
            0
        } else {
            fadeOutAnimationDelayMs
        }
    val alpha by
    animateFloatAsState(
        targetValue = targetAlpha.value,
        animationSpec =
        tween(delayMillis = animationDelayMs, durationMillis = animationDurationMs)
    )
    return motionEventSpy {
        if (it.action == MotionEvent.ACTION_MOVE) {
            if (state.canScrollForward || state.canScrollBackward) {
                targetAlpha.value = visibleAlpha
            }
        } else if (it.action == MotionEvent.ACTION_UP) {
            targetAlpha.value = hiddenAlpha
        }
    }
        .alpha(if (fadeOutEdge) 0.99f else 1f)
        .drawWithContent {
            drawContent()
            val a = size.height - scrollBarHeight.toPx()
            //这一行的好意思是 视口的高度减去进度条的高度
            var x = a / (state.maxValue)
            //用这个值除以控件总高度减去视口的高度， 就可以得到我们窗口实际滚动的距离与进度条实际可以滚动的范围的一个比例
            val offsetY =
                if (state.value > 0) (state.value * x) else 0f
            if (showScrollTrack) {
                drawLine(
                    color = bgColor,
                    start = Offset(
                        size.width - (scrollBarWidth.toPx() - scrollTrackWidth.toPx()) / 2f - scrollTrackWidth.toPx() / 2,
                        0f
                    ),
                    end = Offset(
                        size.width - (scrollBarWidth.toPx() - scrollTrackWidth.toPx()) / 2f - scrollTrackWidth.toPx() / 2,
                        size.height
                    ), strokeWidth = scrollTrackWidth.toPx()
                )
            }
            if (fadeOutEdge) {
                val top = fadeEdgeTop.toPx() / (size.height - state.maxValue)
                val bottom = fadeEdgeBottom.toPx() / (size.height - state.maxValue)
                //如果隐藏边缘属性设置为ture并且列表在滚动中，实现边缘渐隐效果
                drawRect(
                    Brush.verticalGradient(
                        Pair(0.0f, if (state.canScrollBackward) Color.Transparent else Color.Black),
                        Pair(top, Color.Black),
                        Pair(1f - bottom, Color.Black),
                        Pair(1f, if (state.canScrollForward) Color.Transparent else Color.Black),
                        startY = state.value.toFloat(),
                        endY = state.value.toFloat() - state.maxValue + size.height
                    ), blendMode = BlendMode.DstIn
                )
            }

            // Draw the knob
            drawRoundRect(
                color = knobColor,
                topLeft = Offset(
                    size.width - scrollBarWidth.toPx() - scrollMarginRight.toPx(),
                    offsetY
                ),
                size =
                Size(scrollBarWidth.toPx(), scrollBarHeight.toPx()),
                alpha = alpha,
                cornerRadius = CornerRadius(
                    x = knobCornerRadius.toPx(),
                    y = knobCornerRadius.toPx()
                ),
            )

        }
}

@Composable
fun Modifier.columnScrollbar(
    state: LazyListState,
    knobCornerRadius: Dp = 4.dp,
    knobColor: Color = Color(0xFF86776C),
    visibleAlpha: Float = 1f,
    scrollBarPaddingRight: Dp = 20.dp,
    hiddenAlpha: Float = 0f,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 4000,
    scrollBarState: ScrollBarState,
    scrollBarWidth: Dp = 5.dp,
    scrollBarHeight: Dp = 115.dp,
    fadeOutEdge: Boolean = false,
    fadeEdgeTop: Dp = 10.dp,
    fadeEdgeBottom: Dp = 30.dp
): Modifier {
    LaunchedEffect(Unit) {
        val firstItem = state.firstVisibleItemIndex
        val firstItemOffset = state.firstVisibleItemScrollOffset
        state.scrollToItem(index = state.layoutInfo.totalItemsCount - 1)
        state.scrollToItem(0)
        state.scrollToItem(firstItem, firstItemOffset)
    }
    val targetAlpha =
        if (state.isScrollInProgress && (state.canScrollForward || state.canScrollBackward)) {
            visibleAlpha
        } else {
            hiddenAlpha
        }
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
    val alpha by
    animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec =
        tween(delayMillis = animationDelayMs, durationMillis = animationDurationMs)
    )
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
            totalHeight += rect.height
        }
        scrollBarState.childRectMap.forEach { (i, rect) ->
            if (i < offsetIndex) {
                offsetHeight += rect.height
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

        // Draw the knob
        drawRoundRect(
            color = knobColor,
            topLeft = Offset(
                size.width - scrollBarWidth.toPx() - scrollBarPaddingRight.toPx(),
                offsetY
            ),
            size =
            Size(scrollBarWidth.toPx(), scrollBarHeight.toPx()),
            alpha = alpha,
            cornerRadius = CornerRadius(
                x = knobCornerRadius.toPx(),
                y = knobCornerRadius.toPx()
            ),
        )
    }
}

@Composable
fun getScrollBarState(): ScrollBarState {
    val scrollBarState = remember{
        mutableStateOf(ScrollBarState())
    }
    scrollBarState.value.childRectMap = remember {
        mutableStateMapOf()
    }
    return scrollBarState.value
}

class ScrollBarState {
    lateinit var childRectMap: MutableMap<Int, Rect>
}

@Composable
fun Modifier.itemScrollBar(index: Int, scrollBarState: ScrollBarState): Modifier {
    return onPlaced {
        scrollBarState.childRectMap.put(index, it.boundsInParent())
    }
}

@Composable
fun Modifier.gridScrollbar(
    state: LazyGridState,
    knobCornerRadius: Dp = 4.dp,
    scrollBarMarginRight: Dp = 20.dp,
    knobColor: Color = Color.Black,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0f,
    cols: Int = 2,
    footerHeight: Dp = 0.dp,
    footerCount: Int = 0,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 4000,
    scrollBarWidth: Dp = 5.dp,
    scrollBarHeight: Dp = 115.dp,
    fadeOutEdge: Boolean = false,
    fadeEdgeTop: Dp = 30.dp,
    fadeEdgeBottom: Dp = 30.dp
): Modifier {

    val targetAlpha =
        if (state.isScrollInProgress && (state.canScrollForward || state.canScrollBackward)) {
            visibleAlpha
        } else {
            hiddenAlpha
        }
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
    val alpha by
    animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec =
        tween(delayMillis = animationDelayMs, durationMillis = animationDurationMs)
    )
    return alpha(if (fadeOutEdge) 0.99f else 1f).drawWithContent {
        drawContent()
        val count = state.layoutInfo.totalItemsCount - footerCount
        val itemHeight = state.layoutInfo.visibleItemsInfo.first().size.height.toFloat()

        var totalHeight =
            (count / cols + if (count % cols == 0) 0 else 1) * itemHeight + footerHeight.toPx()
        val offsetHeight =
            (state.firstVisibleItemScrollOffset + state.firstVisibleItemIndex / cols * itemHeight).toFloat()

        val a = state.layoutInfo.viewportSize.height - scrollBarHeight.toPx()

        var x = a / (totalHeight - state.layoutInfo.viewportSize.height)

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
        // Draw the knob
        drawRoundRect(
            color = knobColor,
            topLeft = Offset(
                size.width - scrollBarWidth.toPx() - scrollBarMarginRight.toPx(),
                offsetY
            ),
            size =
            Size(scrollBarWidth.toPx(), scrollBarHeight.toPx()),
            alpha = alpha,
            cornerRadius = CornerRadius(
                x = knobCornerRadius.toPx(),
                y = knobCornerRadius.toPx()
            ),
        )
    }
}
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
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.desaysv.jlr.scenarioengine.hmi.widget.container.SampleScrollBarState
import kotlinx.coroutines.launch

@Composable
fun Modifier.gridScrollbarSameItem(
    state: LazyGridState,
    scrollBarState: SampleScrollBarState,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0f,
    cols: Int = 2,
    footerHeight: Dp = 0.dp,
    footerCount: Int = 0,
    scrollBarHeight: Dp = 115.dp,
    fadeOutEdge: Boolean = false,
    fadeEdgeTop: Dp = 30.dp,
    fadeEdgeBottom: Dp = 30.dp,
    spaceHeight: Dp = 0.dp
): Modifier {

    scrollBarState.alpha.value =
        if (state.isScrollInProgress && (state.canScrollForward || state.canScrollBackward) || scrollBarState.isDragging.value) {
            visibleAlpha
        } else {
            hiddenAlpha
        }
    return alpha(if (fadeOutEdge) 0.99f else 1f).drawWithContent {
        drawContent()
        val count = state.layoutInfo.totalItemsCount - footerCount
        val itemHeight =
            if (!state.layoutInfo.visibleItemsInfo.isNullOrEmpty()) state.layoutInfo.visibleItemsInfo.first().size.height.toFloat() else 0f

        var totalHeight =
            (count / cols + if (count % cols == 0) 0 else 1) * itemHeight + footerHeight.toPx() + ((count / cols + if (count % cols == 0) 0 else 1) - 1 + footerCount) * spaceHeight.toPx()
        val offsetHeight =
            (state.firstVisibleItemScrollOffset + state.firstVisibleItemIndex / cols * (itemHeight + spaceHeight.toPx())).toFloat()

        val a = state.layoutInfo.viewportSize.height - scrollBarHeight.toPx()

        var x = a / (totalHeight - state.layoutInfo.viewportSize.height)

        val offsetY =
            if (offsetHeight > 0) (offsetHeight * x) else 0f
        if (scrollBarState.isDragging.value == false) {
            scrollBarState.offsetY.value = offsetY
        }
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
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.gridScrollbarSameItemView(
    state: LazyGridState,
    footerCount: Int = 0,
    footerHeight: Dp = 0.dp,
    cols: Int = 2,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 4000,
    scrollBarState: SampleScrollBarState,
    scrollBarHeight: Dp,
    spaceHeight: Dp = 0.dp
): Modifier {

    val density = LocalDensity.current.density
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
        targetValue = scrollBarState.alpha.value,
        animationSpec =
        tween(delayMillis = animationDelayMs, durationMillis = animationDurationMs)
    )

    val scope = rememberCoroutineScope()
    val draggableState = rememberDraggableState { delta ->
        val count = state.layoutInfo.totalItemsCount - footerCount
        val itemHeight = state.layoutInfo.visibleItemsInfo.first().size.height.toFloat()

//        var totalHeight =
//            (count / cols + if (count % cols == 0) 0 else 1) * itemHeight + footerHeight.value * density
        var totalHeight =
            (count / cols + if (count % cols == 0) 0 else 1) * itemHeight + footerHeight.value * density + ((count / cols + if (count % cols == 0) 0 else 1) - 1 + footerCount) * spaceHeight.value * density

        val a = state.layoutInfo.viewportSize.height - scrollBarHeight.value * density

        var x = a / (totalHeight - state.layoutInfo.viewportSize.height)
        //用这个值除以控件总高度减去视口的高度， 就可以得到我们窗口实际滚动的距离与进度条实际可以滚动的范围的一个比例
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

package com.compose.demo.widget

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.scrollbar(
    state: LazyListState,
    knobCornerRadius: Dp = 4.dp,
    knobColor: Color = Color(0xFF86776C),
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0f,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 4000,
    scrollBarState: ScrollBarState,
    scrollBarWidth: Dp = 5.dp,
    scrollBarHeight: Dp = 115.dp,
): Modifier {
    LaunchedEffect(Unit) {
        state.scrollToItem(index = state.layoutInfo.totalItemsCount - 1)
        state.scrollToItem(0)
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
    return drawWithContent {
        drawContent()
        var totalHeight = 0f
        var offsetOne = 0f
        var offsetIndex = 0
        var offsetHeight = 0f
        scrollBarState.childRectMap.toList().forEach {
            val rect = it.second
            if (it.first >= offsetIndex && rect.top < 0 && it.first < state.firstVisibleItemIndex + state.layoutInfo.visibleItemsInfo.size) {
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


        // Draw the knob
        drawRoundRect(
            color = knobColor,
            topLeft = Offset(size.width - 10f, offsetY),
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
    val scrollBarState = ScrollBarState()
    scrollBarState.childRectMap = remember {
        mutableStateMapOf()
    }
    return scrollBarState
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
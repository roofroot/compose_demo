package com.desaysv.jlr.scenarioengine.hmi.widget.container

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
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
                animationSpec = tween(delayMillis = 4000, durationMillis = 500)
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
                        scrollBarColor, shape = RoundedCornerShape(CornerSize(scrollBarWidth / 2))
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
    scrollBarState: SampleScrollBarState,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0f,
    scrollBarHeight: Dp = 115.dp,
): Modifier {
    val density = LocalDensity.current.density
    return motionEventSpy {
        if (it.action == MotionEvent.ACTION_MOVE) {
            if (state.maxValue != Int.MAX_VALUE && state.maxValue > 0) {
                scrollBarState.alpha.value = visibleAlpha
            }
        } else if (it.action == MotionEvent.ACTION_UP) {
            scrollBarState.alpha.value = hiddenAlpha
        } else if (it.action == MotionEvent.ACTION_CANCEL) {
            scrollBarState.alpha.value = hiddenAlpha
        }
    }
        .onSizeChanged {
            scrollBarState.viewHeight.value = it.height.toFloat()
        }
        .drawWithContent {
            drawContent()
            if (!scrollBarState.isDragging.value && state.maxValue != 0 && state.maxValue != Int.MAX_VALUE && state.maxValue > 0) {
            val a = scrollBarState.viewPortHeight.value - scrollBarHeight.value * density
            //这一行的好意思是 视口的高度减去进度条的高度
            var x = a / (state.maxValue)
            //用这个值除以控件总高度减去视口的高度， 就可以得到我们窗口实际滚动的距离与进度条实际可以滚动的范围的一个比例
            val offsetY = state.value * x
            scrollBarState.offsetY.value = offsetY
        }
//            if (showScrollTrack) {
//                drawLine(
//                    color = bgColor, start = Offset(
//                        size.width - (scrollBarWidth.toPx() - scrollTrackWidth.toPx()) / 2f - scrollTrackWidth.toPx() / 2,
//                        0f
//                    ), end = Offset(
//                        size.width - (scrollBarWidth.toPx() - scrollTrackWidth.toPx()) / 2f - scrollTrackWidth.toPx() / 2,
//                        size.height
//                    ), strokeWidth = scrollTrackWidth.toPx()
//                )
//            }
//            if (fadeOutEdge) {
//                val top = fadeEdgeTop.toPx() / (size.height - state.maxValue)
//                val bottom = fadeEdgeBottom.toPx() / (size.height - state.maxValue)
//                //如果隐藏边缘属性设置为ture并且列表在滚动中，实现边缘渐隐效果
//                drawRect(
//                    Brush.verticalGradient(
//                        Pair(0.0f, if (state.canScrollBackward) Color.Transparent else Color.Black),
//                        Pair(top, Color.Black),
//                        Pair(1f - bottom, Color.Black),
//                        Pair(1f, if (state.canScrollForward) Color.Transparent else Color.Black),
//                        startY = state.value.toFloat(),
//                        endY = state.value.toFloat() - state.maxValue + size.height
//                    ), blendMode = BlendMode.DstIn
//                )
//            }
        }
}

@Composable
fun Modifier.fadeOutEdge(
    state: ScrollState,
    fadeEdgeTop: Dp = 10.dp,
    fadeEdgeBottom: Dp = 30.dp,
): Modifier {

    return alpha(0.99f).drawWithContent {
        drawContent()
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
}


@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.scrollbarView(
    state: ScrollState,
    scrollBarState: SampleScrollBarState,
    scrollBarHeight: Dp = 115.dp,

    ): Modifier {
    val density = LocalDensity.current.density
    val scope = rememberCoroutineScope()
    val draggableState = rememberDraggableState { delta ->
        //这一行的好意思是 视口的高度减去进度条的高度
//        var x = a / (state.maxValue)
        //用这个值除以控件总高度减去视口的高度， 就可以得到我们窗口实际滚动的距离与进度条实际可以滚动的范围的一个比例
        if (state.canScrollForward && delta > 0 || state.canScrollBackward && delta < 0) {
            val a = scrollBarState.viewPortHeight.value - scrollBarHeight.value * density
            if (scrollBarState.offsetY.value + delta < 0) {
                scrollBarState.offsetY.value = 0f
            } else if (scrollBarState.offsetY.value + delta > a) {
                scrollBarState.offsetY.value = a
            } else {
                scrollBarState.offsetY.value += delta
            }
            scope.launch {
                val scrollDistance = (delta * state.maxValue) / a
                state.scrollBy(scrollDistance)
            }
        }
    }
    return graphicsLayer {
        translationY = scrollBarState.offsetY.value
        alpha = scrollBarState.alphaState.value
    }
        .pointerInput(Unit) {
            detectTapGestures(onPress = {
                scrollBarState.isDragging.value = true
            }, onTap = {
                scrollBarState.isDragging.value = false
            })
        }
        .draggable(draggableState, orientation = Orientation.Vertical, onDragStarted = {
            scrollBarState.isDragging.value = true
            scrollBarState.alpha.value = 1f
        }, onDragStopped = {
            scrollBarState.isDragging.value = false
            scrollBarState.alpha.value = 0f
        })

}

@Composable
fun getSampleScrollBarState(
    visibleAlpha: Float = 1f,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 4000,
): SampleScrollBarState {
    val scrollBarState = remember {
        mutableStateOf(SampleScrollBarState())
    }
    scrollBarState.value.offsetY = remember {
        mutableStateOf(0f)
    }
    scrollBarState.value.viewHeight = remember {
        mutableStateOf(0f)
    }
    scrollBarState.value.viewPortHeight = remember {
        mutableStateOf(0f)
    }
    scrollBarState.value.isDragging = remember {
        mutableStateOf(false)
    }
    scrollBarState.value.alpha = remember {
        mutableStateOf(0f)
    }
    val animationDurationMs = if (scrollBarState.value.alpha.value == visibleAlpha) {
        fadeInAnimationDurationMs
    } else {
        fadeOutAnimationDurationMs
    }
    val animationDelayMs = if (scrollBarState.value.alpha.value == visibleAlpha) {
        0
    } else {
        fadeOutAnimationDelayMs
    }
    scrollBarState.value.alphaState = animateFloatAsState(
        targetValue = scrollBarState.value.alpha.value,
        animationSpec = tween(delayMillis = animationDelayMs, durationMillis = animationDurationMs)
    )
    return scrollBarState.value
}

class SampleScrollBarState {
    lateinit var offsetY: MutableState<Float>
    lateinit var isDragging: MutableState<Boolean>
    lateinit var viewHeight: MutableState<Float>
    lateinit var viewPortHeight: MutableState<Float>
    lateinit var alpha: MutableState<Float>
    lateinit var alphaState: State<Float>
}

@Composable
fun Modifier.parentScrollBar(scrollBarState: SampleScrollBarState): Modifier {
    return onSizeChanged {
        scrollBarState.viewPortHeight.value = it.height.toFloat()
    }
}

@Composable
fun BoxScope.SimpleScrollBar(modifier: Modifier, isDragging: Boolean) {
    Box(
        modifier = modifier
            .align(Alignment.TopEnd)
            .width(45.dp)
            .height(115.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .width(5.dp)
                .fillMaxHeight()
                .background(
                    if (isDragging) Color.Black else Color(0xFF86776C),
                    shape = RoundedCornerShape(2.5f)
                )
        ) {

        }
    }
}




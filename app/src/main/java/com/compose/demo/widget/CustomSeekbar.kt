package com.compose.demo.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.compose.demo.shape.TriangleShape
import kotlin.math.roundToInt

/**
 * @param modifier 的长宽等为最外层的属性，比如给进度条整体增加边框，设置进图条的占位宽高等
 * @param currentProgress 当前进度
 * @param totalProgress 总进度
 * @param thumbColor 按钮颜色，如果没有自定义thumbContent，可以使用这个参数修改默认样式的按钮颜色
 * @param fgOffset 默认的进度条前景的宽度是在进度条的左侧，紧挨进度条的位置，如果有圆形的按钮之类的情况，会露出前景的边沿，因此需要设置一下这个偏移量
 *                 让进度条按钮刚好可以遮挡住进度条
 * @param barHeight 进度条的bar的前景和背景的高度
 * @param barBgColor 进度条背景色，如果没有自定义barBgContent,可以使用这个参数修改默认样式的进度条背景色
 * @param barFgColor 进度条前景色，如果没有自定义barFgContent,可以使用这个参数修改默认样式的进度条前景色
 * @param barBgContent 自定义的进度条背景
 * @param barFgContent  @Composable (width: Dp) 自定义的进度条前景，需要将width传递给进自定义前景的宽度参数、
 *                     ，这样才能让进度条的前景随着拖动按钮变化
 * @param thumbContent  @Composable (modifier: Modifier) 需要使用modifier,作为自定义thumb的modifier，
 *                      这样才能使按钮随着拖动改变位置
 */
@Composable
fun CustomSeekBar(
    modifier: Modifier,
    currentProgress: MutableState<Int>,
    totalProgress: Int = 100,
    thumbColor: Color = Color.Blue,
    fgOffset: Dp = 10.dp,
    barHeight: Dp = 10.dp,
    barBgColor: Color = Color.LightGray,
    barFgColor: Color = Color.Blue,
    onStop: (progress: Int) -> Unit = {},
    barBgContent: @Composable () -> Unit = {
        Box(
            Modifier
                .clip(RoundedCornerShape(barHeight / 2))
                .background(barBgColor)
                .fillMaxWidth()
                .height(barHeight)
        ) {

        }
    },
    barFgContent: @Composable (width: Dp) -> Unit = {
        Box(
            Modifier
                .clip(RoundedCornerShape(barHeight / 2))
                .width(it)
                .height(barHeight)
                .background(barFgColor)
        ) {

        }
    },
    thumbContent: @Composable (modifier: Modifier) -> Unit = {
        Box(
            it
                .shadow(5.dp, shape = CircleShape)
                .width(10.dp)
                .height(18.dp)
                .clip(CircleShape)
                .background(thumbColor)
        ) {

        }
    }
) {
    val size = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val fgWidth = remember {
        mutableStateOf(0.dp)
    }
    val animFgWidth = animateDpAsState(targetValue = fgWidth.value)
    val offset = remember {
        mutableStateOf(0f)
    }
    val thumbSizePx = remember {
        mutableStateOf(0f)
    }

    val animOffset = animateIntAsState(targetValue = offset.value.roundToInt())
    val density = LocalDensity.current.density
    offset.value = (size.value.width - thumbSizePx.value) / totalProgress * currentProgress.value
    fgWidth.value = (offset.value / density).dp + fgOffset
    val state = rememberDraggableState(onDelta = {
        if (offset.value + it > 0 && offset.value + it < size.value.width - thumbSizePx.value) {
            offset.value += it
        }
        fgWidth.value = (offset.value / density).dp + fgOffset
        currentProgress.value =
            (totalProgress * (offset.value / (size.value.width - thumbSizePx.value))).roundToInt()
    })
    var modifierThumb = Modifier
        .onPlaced {
            thumbSizePx.value = it.size.width.toFloat()
        }
        .offset {
            IntOffset(animOffset.value, 0)
        }
        .draggable(
            state = state,
            orientation = Orientation.Horizontal,
            onDragStopped = { onStop.invoke(currentProgress.value) })
    Box(
        modifier
            .onPlaced {
                size.value = it.size
            }, contentAlignment = Alignment.CenterStart
    ) {
        barBgContent()
        barFgContent(animFgWidth.value)
        thumbContent(modifierThumb)
    }
}

/**
 * @param modifier 的长宽等为最外层的属性，比如给进度条整体增加边框，设置进图条的占位宽高等
 * @param currentProgress 当前进度
 * @param totalProgress 总进度
 * @param step 步长,最小的可移动刻度
 * @param lineStep 如果在没有自定义背景的情况下可以使用这个参数设置背景长线的间隔的刻度
 * @param thumbColor 按钮颜色，如果没有自定义thumbContent，可以使用这个参数修改默认样式的按钮颜色
 * @param barHeight 进度条的bar的前景和背景的高度
 * @param barBgColor 进度条背景色，如果没有自定义barBgContent,可以使用这个参数修改默认样式的进度条背景色
 * @param barBgContent 自定义的进度条背景
 * @param thumbContent  @Composable (modifier: Modifier) 需要使用modifier,作为自定义thumb的modifier，
 *                      这样才能使按钮随着拖动改变位置
 */
@Composable
fun CustomStepSeekBar(
    modifier: Modifier,
    currentProgress: MutableState<Int>,
    totalProgress: Int = 100,
    step: Int = 10,
    lineStep: Int = 10,
    thumbColor: Color = Color.Blue,
    barHeight: Dp = 10.dp,
    barBgColor: Color = Color.LightGray,
    barBgContent: @Composable () -> Unit = {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(barHeight), onDraw = {
            val sStep = size.width / totalProgress
            val bStep = sStep * lineStep
            val mStep = sStep * step
            var offsetX = 0f
            while (offsetX <= size.width) {

                drawLine(
                    color = barBgColor,
                    Offset(offsetX, size.height - (size.height) * 0.2f),
                    Offset(offsetX, size.height),
                    strokeWidth = 1f
                )

                offsetX += sStep
            }
            offsetX = 0f
            while (offsetX <= size.width) {

                drawLine(
                    color = barBgColor,
                    Offset(offsetX, size.height - (size.height) * 0.35f),
                    Offset(offsetX, size.height),
                    strokeWidth = 1f
                )

                offsetX += mStep
            }
            offsetX = 0f;
            while (offsetX <= size.width) {
                drawLine(
                    color = barBgColor,
                    Offset(offsetX, size.height - (size.height) * 0.5f),
                    Offset(offsetX, size.height),
                    strokeWidth = 2f
                )
                offsetX += bStep
            }
        })
    },
    thumbContent: @Composable (modifier: Modifier) -> Unit = {
        Box(
            it
                .height(barHeight)
                .padding(top = 5.dp)
                .width(30.dp), contentAlignment = Alignment.TopCenter
        ) {
            Box(
                Modifier
                    .shadow(5.dp, shape = TriangleShape())
                    .width(10.dp)
                    .height(8.dp)
                    .clip(TriangleShape())
                    .background(thumbColor)
            ) {

            }
        }

    }
) {
    val size = remember {
        mutableStateOf(IntSize(0, 0))
    }
    val fgWidth = remember {
        mutableStateOf(0.dp)
    }
    val animFgWidth = animateDpAsState(targetValue = fgWidth.value)
    val offset = remember {
        mutableStateOf(0f)
    }
    val thumbSizePx = remember {
        mutableStateOf(0f)
    }

    val animOffset = animateIntAsState(targetValue = offset.value.roundToInt())
    offset.value = (size.value.width.toFloat()) / totalProgress * currentProgress.value
    val interactionSource = remember {
        MutableInteractionSource()
    }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            when (it) {
                is DragInteraction.Stop -> {
                    currentProgress.value =
                        ((currentProgress.value.toFloat() / step).roundToInt() * step)
                }
            }
        }
    }

    val state = rememberDraggableState(onDelta = {
        if (offset.value + it > 0 && offset.value + it < size.value.width) {
            offset.value += it
        }
        currentProgress.value =
            (totalProgress * (offset.value / (size.value.width))).roundToInt()
    })
    var modifierThumb = Modifier
        .onPlaced {
            thumbSizePx.value = it.size.width.toFloat()
        }
        .offset {
            IntOffset(animOffset.value - (thumbSizePx.value / 2).toInt(), 0)
        }
        .draggable(
            state = state,
            orientation = Orientation.Horizontal,
            interactionSource = interactionSource
        )
    Box(
        modifier
            .onPlaced {
                size.value = it.size
            }, contentAlignment = Alignment.CenterStart
    ) {
        barBgContent()
        thumbContent(modifierThumb)
    }
}
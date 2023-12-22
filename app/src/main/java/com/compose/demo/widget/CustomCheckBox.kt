package com.compose.demo.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import kotlin.io.path.Path

/**
 * @param dotSize 圆点的大小
 * @param strokeSize 外框的大小
 * @param strokeWidthDp 外框的宽度
 * @param radioButtonSize 整个radioButton的大小，也就是可点击区域大小
 * @param enabledColor 可点击状态的颜色
 * @param disableColor 不可点击状态的颜色
 */
@Composable
fun CustomCheckBox(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: (() -> Unit)?,
    dotSize: Dp = 12.dp,
    strokeSize: Dp = 20.dp,
    strokeWidthDp: Dp = 2.dp,
    radioButtonSize: Dp = 40.dp,
    enabledColor: Color = Color.Black,
    disableColor: Color = Color.Gray,
    enabled: Boolean = true,
    checkMarkColor: Color = Color.Gray,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val radioColor = if (enabled) enabledColor else disableColor
    val pathLength = remember {
        mutableStateOf(if (selected) 1f else 0f)
    }
    val animPath = animateFloatAsState(targetValue = pathLength.value)
    val selectableModifier =
        if (onClick != null) {
            Modifier.selectable(
                selected = selected,
                onClick = {
                    val state = !selected
                    if (state) pathLength.value = 1f else pathLength.value = 0f
                    onClick()
                },
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = radioButtonSize / 2
                )
            )
        } else {
            Modifier
        }
    Canvas(
        modifier
            .then(selectableModifier)
            .size(radioButtonSize)
            .wrapContentSize(Alignment.Center)
            .requiredSize(radioButtonSize)
    ) {
        // Draw the radio button
        val strokeWidth = strokeWidthDp.toPx()
        val path2 = Path()

        path2.moveTo(
            (size.width + strokeSize.toPx()) / 2 - strokeWidth / 2,
            (size.height - strokeSize.toPx()) / 2 - strokeWidth / 2 + strokeSize.toPx() / 10
        )
        path2.lineTo(
            (size.width + strokeSize.toPx()) / 2 - strokeWidth / 2,
            (size.height - strokeSize.toPx()) / 2 - strokeWidth / 2
        )
        path2.lineTo(
            (size.width - strokeSize.toPx()) / 2 - strokeWidth / 2,
            (size.height - strokeSize.toPx()) / 2 - strokeWidth / 2
        )
        path2.lineTo(
            (size.width - strokeSize.toPx()) / 2 - strokeWidth / 2,
            (size.height + strokeSize.toPx()) / 2 - strokeWidth / 2
        )
        path2.lineTo(
            (size.width + strokeSize.toPx()) / 2 - strokeWidth / 2,
            (size.height + strokeSize.toPx()) / 2 - strokeWidth / 2
        )
        if (selected) {
            path2.lineTo(
                (size.width + strokeSize.toPx()) / 2 - strokeWidth / 2,
                (size.height - strokeSize.toPx()) / 2 - strokeWidth / 2 + strokeSize.toPx() * 2 / 3
            )
        } else {
            path2.close()
        }


        drawPath(
            path = path2,
            color = radioColor,
            style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        val pathMeasure = PathMeasure()
        val path = Path()

        path.moveTo(size.width / 2 - strokeSize.toPx() / 4, size.height / 2)
        path.lineTo(size.width / 2, size.height / 2 + strokeSize.toPx() / 5)
        path.lineTo(
            size.width / 2 + strokeSize.toPx() * 3 / 4,
            (size.height - strokeSize.toPx()) / 2
        )
        pathMeasure.setPath(path = path, false)
        val tempPath = Path()
        tempPath.rewind()
        pathMeasure.getSegment(0f, pathMeasure.length * animPath.value, tempPath, true)
        drawPath(
            tempPath,
            checkMarkColor,
            style = Stroke(5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Preview
@Composable
fun previewCheckBox() {
    val select = remember {
        mutableStateOf(false)
    }
    CustomCheckBox(selected = select.value, onClick = {
        select.value = !select.value
    })
//    val image =
//        AnimatedImageVector.animatedVectorResource(R.drawable.check_box_anim)
//    Image(
//        modifier = Modifier.clickable {
//            select.value = !select.value
//        },
//        painter = rememberAnimatedVectorPainter(image, select.value),
//        contentDescription = "",
//        contentScale = ContentScale.Crop
//    )
}

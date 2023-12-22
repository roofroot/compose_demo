package com.compose.demo.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @param dotSize 圆点的大小
 * @param strokeSize 外框的大小
 * @param strokeWidthDp 外框的宽度
 * @param radioButtonSize 整个radioButton的大小，也就是可点击区域大小
 * @param enabledColor 可点击状态的颜色
 * @param disableColor 不可点击状态的颜色
 */
@Composable
fun CustomRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: (() -> Unit)?,
    dotSize: Dp = 12.dp,
    strokeSize: Dp = 20.dp,
    strokeWidthDp: Dp = 2.dp,
    radioButtonSize: Dp = 30.dp,
    enabledColor: Color = Color.Black,
    disableColor: Color = Color.Gray,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val dotRadius = animateDpAsState(
        targetValue = if (selected) dotSize / 2 else 0.dp,
        animationSpec = tween(durationMillis = 100)
    )
    val radioColor = if (enabled) enabledColor else disableColor
    val selectableModifier =
        if (onClick != null) {
            Modifier.selectable(
                selected = selected,
                onClick = onClick,
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
        drawCircle(
            radioColor,
            radius = (strokeSize / 2).toPx() - strokeWidth / 2,
            style = Stroke(strokeWidth)
        )
        if (dotRadius.value > 0.dp) {
            drawCircle(radioColor, dotRadius.value.toPx() - strokeWidth / 2, style = Fill)
        }
    }
}

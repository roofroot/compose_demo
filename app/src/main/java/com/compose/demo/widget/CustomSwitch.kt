package com.compose.demo.widget

import android.graphics.drawable.shapes.Shape
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val AnimationSpec = TweenSpec<Float>(durationMillis = 100)

@Composable
fun CustomSwitch(
    switchWidth: Dp,
    switchHeight: Dp,
    thumbSize: Dp,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    backgroundShape: RoundedCornerShape = RoundedCornerShape(
        switchHeight / 2,
        switchHeight / 2,
        switchHeight / 2,
        switchHeight / 2
    ),
    backgroundColorUncheck: Color= Color.Gray,
    backgroundColorChecked: Color=Color.Black,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {

    val minBound = with(LocalDensity.current) { 0f }
    val maxBound = with(LocalDensity.current) { switchWidth.toPx() - thumbSize.toPx() }
    val valueToOffset = remember<(Boolean) -> Float>(minBound, maxBound) {
        { value -> if (value) maxBound else minBound }
    }

    val targetValue = valueToOffset(checked)
    val offset = remember { Animatable(targetValue) }
    val scope = rememberCoroutineScope()

    SideEffect {
        // min bound might have changed if the icon is only rendered in checked state.
        offset.updateBounds(lowerBound = minBound)
    }

    DisposableEffect(checked) {
        if (offset.targetValue != targetValue) {
            scope.launch {
                offset.animateTo(targetValue, AnimationSpec)
            }
        }
        onDispose { }
    }

    // TODO: Add Swipeable modifier b/223797571
    val toggleableModifier =
        if (onCheckedChange != null) {
            Modifier.toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            )
        } else {
            Modifier
        }

    Box(
        modifier
            .width(switchWidth)
            .height(switchHeight)
            .background(
                if (checked) backgroundColorChecked else backgroundColorUncheck,
                backgroundShape
            )
            .then(toggleableModifier)
            .wrapContentSize(Alignment.Center)
            .requiredSize(switchWidth, switchHeight)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(offset.asState().value.roundToInt(), 0) }
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = false, switchHeight / 2)
                )
                .requiredSize(thumbSize),
            contentAlignment = Alignment.Center
        ) {
            thumbContent?.invoke()
        }
    }
}

@Preview
@Composable
fun CustomSwitchPreview(){

}
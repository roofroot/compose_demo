package com.compose.demo.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp

@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    width: Dp,
    spaceV: Dp,
    spaceH: Dp,
    content: @Composable () -> Unit
) {

    Layout(modifier = modifier, content = content) { measurables, constraints ->
        //每个元素的x轴位置
        val leftLocations = Array(measurables.size) { 0 }
        //每个元素的y轴位置
        val topLocations = Array(measurables.size) { 0 }
        var top = 0;
        var left = 0;
        var childHeight = 0;
        val placeables = measurables.mapIndexed { index, measurable ->
            //测量每一个元素的宽高
            val placeable = measurable.measure(constraints)
            if (left + placeable.width + spaceH.roundToPx() < width.roundToPx()) {
                leftLocations[index] = left
            } else {
                left = 0
                leftLocations[index] = left
                top += placeable.height + spaceV.roundToPx()
            }
            topLocations[index] = top
            childHeight = placeable.height
            left += placeable.width + spaceH.roundToPx()

            placeable
        }
        layout(width.roundToPx(), top + childHeight) {
            placeables.forEachIndexed { index, placeable ->
                //重置每一个元素的宽高
                placeable.placeRelative(x = leftLocations[index], topLocations[index])
            }
        }
    }

}
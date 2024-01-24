package com.compose.demo.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 *
 * @param modifier Modifier
 * @param columns Int
 * @param rows Int
 * @param spaceV Dp
 * @param spaceH Dp
 * @param fixed Int 0,item的宽度高度都按照总高度与数量平均，1，item的宽度按照总宽度与总数平均，
 * 2，item的宽度按照总高度与数量平均，默认值-1不需要自适应
 * @param content [@androidx.compose.runtime.Composable] Function0<Unit>
 */
@Composable
fun GridLayout(
    modifier: Modifier = Modifier,
    columns: Int,
    rows: Int = -1,
    spaceV: Dp = 0.dp,
    spaceH: Dp = 0.dp,
    fixed: Int = -1,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        if (measurables.size == 0) {
            layout(0, 0) {

            }
        } else {
            //每个元素的x轴位置
            val leftLocations = Array(measurables.size) { 0 }
            //每个元素的y轴位置
            val topLocations = Array(measurables.size) { 0 }
            var top = 0
            var left = 0
            var mRows: Int
            if (rows == -1) {
                mRows = measurables.size / columns
                if (mRows == 0) {
                    mRows = 1
                }
            } else {
                mRows = rows
            }
            var childHeight =
                ((constraints.maxHeight - spaceV.toPx() * (mRows - 1)) / mRows).roundToInt()
            var childWidth =
                ((constraints.maxWidth - spaceH.toPx() * (columns - 1)) / columns).roundToInt()
            if (childWidth <= 0) {
                childWidth = 10
            }
            if (childHeight <= 0) {
                childHeight = 10
            }
            var count = 0
            var maxLeft = 0
            val placeables = measurables.mapIndexed { index, measurable ->
                //测量每一个元素的宽高
                var placeable: Placeable

                when (fixed) {
                    0 -> {
                        try {
                            placeable = measurable.measure(
                                Constraints.fixed(childWidth, childHeight)
                            )
                        } catch (e: Exception) {
                            placeable = measurable.measure(
                                Constraints.fixedWidth(childWidth)
                            )
                            childHeight = placeable.measuredHeight
                        }
                    }

                    1 -> {
                        placeable = measurable.measure(
                            Constraints.fixedWidth(childWidth)
                        )
                        childHeight = placeable.measuredHeight
                    }

                    2 -> {
                        placeable = measurable.measure(
                            Constraints.fixedHeight(childHeight)
                        )
                        childWidth = placeable.measuredWidth
                    }

                    else -> {
                        placeable = measurable.measure(constraints)
                        childWidth = placeable.measuredWidth
                        childHeight = placeable.measuredHeight
                    }
                }



                count++
                if (count <= columns) {
                    leftLocations[index] = left
                } else {
                    left = 0
                    count = 1
                    leftLocations[index] = left
                    top += childHeight + spaceV.roundToPx()
                }
                topLocations[index] = top

                left += childWidth + spaceH.roundToPx()
                if (maxLeft < left) {
                    maxLeft = left
                }
                placeable
            }
            layout(maxLeft, top + childHeight) {
                placeables.forEachIndexed { index, placeable ->
                    //重置每一个元素的宽高
                    placeable.placeRelative(x = leftLocations[index], topLocations[index])
                }
            }
        }
    }

}
package com.compose.demo.layout

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

@Composable
fun PanelLayout(
    modifier: Modifier = Modifier,
    minSize: Dp,
    width: Dp,
    content: @Composable () -> Unit
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val cellMap = HashMap<Int, HashMap<Int, Boolean>>()
        //每个元素的x轴位置
        val leftLocations = Array(measurables.size) { 0 }
        //每个元素的y轴位置
        val topLocations = Array(measurables.size) { 0 }

        val maxColumnCellCount = (width / minSize).roundToInt();
        var maxRow = 0;

        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)
            val rowCellCount = (placeable.height / minSize.toPx()).roundToInt()
            val columnCellCount = (placeable.width / minSize.toPx()).roundToInt()
            var startRowCell = -1
            var startColumnCell = -1
            var endRowCell = -1
            var endColumnCell = -1
            var scanRow = 0
            Log.e("aaaaaaaaaaa",columnCellCount.toString())
            while (true) {
                if (cellMap.get(scanRow) != null && cellMap.get(scanRow)!!.size + columnCellCount > maxColumnCellCount) {
                    scanRow++
                    continue
                }
                for (cola in 0..maxColumnCellCount - columnCellCount) {
                    var columnCount = 0
                    for (colb in cola until cola + columnCellCount) {
                        if (cellMap.get(scanRow)?.get(colb) == null) {
                            columnCount++
                            if (columnCount == 1) {
                                startColumnCell = colb
                            }
                        } else {
                            columnCount = 0
                        }
                        if (columnCount == columnCellCount) {
                            endColumnCell = colb
                            break
                        }
                    }
                    if (endColumnCell != -1) {
                        break
                    }
                }
                var rowCount = 0;
                for (rowa in scanRow ..scanRow + rowCellCount) {
                    var columnCount = 0
                    for (col in startColumnCell..endColumnCell) {
                        if (cellMap.get(rowa)?.get(col) == null) {
                            columnCount++
                        } else {
                            columnCount = 0
                        }
                        if (columnCount == columnCellCount) {
                            rowCount++
                            break
                        }
                    }
                    if (rowCount == rowCellCount) {
                        startRowCell = scanRow
                        endRowCell = scanRow + rowCellCount - 1
                        break
                    }
                }
                if (startRowCell != -1) {
                    break
                } else {
                    scanRow++
                    startColumnCell = -1
                    endColumnCell = -1
                }
            }
            for (row in startRowCell..endRowCell) {
                if (maxRow < row) {
                    maxRow = row
                }
                for (col in startColumnCell..endColumnCell) {
                    if (cellMap.get(row) == null) {
                        cellMap.put(row, HashMap())
                    }
                    cellMap.get(row)?.put(col, true)
                }
            }
            leftLocations[index] = (startColumnCell * minSize.toPx()).roundToInt()
            topLocations[index] = (startRowCell * minSize.toPx()).roundToInt()
            placeable
        }
        layout(width.roundToPx(), ((maxRow+1) * minSize.toPx()).roundToInt()) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x = leftLocations[index], topLocations[index])
            }
        }
    }

}
package com.compose.demo.widget

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HealthCurveChart(data: List<Float>) {
    val checkedState = remember {
        mutableStateListOf(true, true, true, true, true, true)
    }
    val lines = remember {
        mutableStateListOf<HealthLineOption>()
    }
    val nowPoints = remember {
        mutableStateListOf<Float>(0f, 0f, 0f, 0f, 0f, 0f)
    }
    val mData = remember {
        mutableStateOf(data)
    }
    val scope = rememberCoroutineScope()

    mData.value = data
//    data.forEachIndexed { index, fl ->
//        if (nowPoints.size - 1 >= index) {
//            nowPoints.set(index, fl)
//        } else {
//            nowPoints.add(fl)
//        }
//    }
    val pathOffset = remember {
        mutableStateOf(1f)
    }
    val time = remember {
        mutableStateOf(-1L)
    }

    val normalNumList = listOf(
        Pair(60f, 100f), Pair(102f, 180f),
        Pair(12f, 30f), Pair(90f, 140f), Pair(60f, 90f), Pair(95f, 100f)
    )
    val heartRateUnit = getLowHighUnit(normalNumList[0].first, normalNumList[0].second)
    val heartRateVariabilityUnit = getLowHighUnit(normalNumList[1].first, normalNumList[1].second)
    val respiratoryRateUnit = getLowHighUnit(normalNumList[2].first, normalNumList[2].second)
    val systolicBloodPressureUnit = getLowHighUnit(normalNumList[3].first, normalNumList[3].second)
    val diastolicBloodPressure = getLowHighUnit(normalNumList[4].first, normalNumList[4].second)
    val bloodOxygenSaturationUnit = getLowHighUnit(normalNumList[5].first, normalNumList[5].second)
    val unitList = listOf(
        heartRateUnit,
        heartRateVariabilityUnit,
        respiratoryRateUnit,
        systolicBloodPressureUnit,
        diastolicBloodPressure,
        bloodOxygenSaturationUnit
    )
    val colors = listOf<Color>(
        Color(0xff4560E5), Color(0xffE54545),
        Color(0xffE59545), Color(0xff95E545),
        Color(0xff45B0E5), Color(0xff7A45E5)
    )
    scope.launch {
        if (lines.isNullOrEmpty()) {
            for (i in 0..5) {
                val list = ArrayList<PointF>()
//            var a = 0
//            while (a <1000) {
//                list.add(Point(a, Random.nextInt(i * 100, i * 100 + 200)))
//                a += 100
//            }
                val lineOption = HealthLineOption(
                    list, colors[i], unitList[i].first, unitList[i].second
                )
                lines.add(lineOption)
            }
        }

        if (pathOffset.value >= 1f) {
//                Log.e(
//                    "time", "time dsf:${System.currentTimeMillis() - time.value} " +
//                            "data:${mData.value}"
//                )
            time.value = System.currentTimeMillis()
            val points = ArrayList<HealthLineOption>()
            for (i in 0 until lines.size) {
                val list = lines.get(i).points.toMutableList()
                if (list.size > 11) {
                    list.removeAt(0)
                }
                list.add(PointF(y = mData.value.get(i)))
                val lineOption = lines.get(i).copy(points = list)
                points.add(lineOption)
            }
            lines.clear()
            lines.addAll(points)
            pathOffset.value = 0f

            while (pathOffset.value < 1f) {
                delay(100)
                pathOffset.value =
                    if ((pathOffset.value + 0.1f) > 1f) 1f else (pathOffset.value + 0.1f)
            }
            for (i in 0 until lines.size) {
                val list = lines.get(i).points
                nowPoints.set(i, list.get(list.size - 1).y.toFloat())
            }

        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Spacer(modifier = Modifier.height(29.dp))

    Row(Modifier.height(200.dp), verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(16.dp))
        CurveChart(
            modifier = Modifier
                .width(445.dp)
                .height(141.dp),
            pointLists = lines,
            checkedState = checkedState,
            totalXUnit = 1000f,
            pathLength = pathOffset.value,
            stepLineEndPadding = 0.dp
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
}




fun getLowHighUnit(normalLow: Float, normalHigh: Float): Pair<Float, Float> {
    val l = normalLow - (normalHigh - normalLow) * 2f / 5f
    val h = normalHigh + (normalHigh - normalLow) * 2f / 5f
    return Pair(l, h)
}

private fun getType(num: Float, numScope: Pair<Float, Float>): Int {
    return if (num >= numScope.first && num <= numScope.second) {
        0
    } else if (num < numScope.first) {
        -1
    } else {
        1
    }
}

data class HealthLineOption(
    val points: List<PointF>,
    val color: Color,
    val LowUnit: Float,
    val HighUnit: Float,
)

data class PointF(val x: Float = 0f, val y: Float = 0f)

@Composable
private fun CurveChart(
    modifier: Modifier = Modifier.fillMaxSize(),
    pointLists: List<HealthLineOption>,
    checkedState: MutableList<Boolean>,
    totalXUnit: Float,
    showStepSegmentLine: Boolean = true,
    stepSegmentLineColor: Color = Color(0xe6ffffff),
    stepSegmentLineWidth: Dp = 1.dp,
    curveLineWidth: Dp = 1.dp,
    pathLength: Float = 1f,
    stepLineEndPadding: Dp = 10.dp
) {
    Canvas(modifier, onDraw = {
        drawIntoCanvas {
            val offset = size.height / 5
            if (showStepSegmentLine) {
                drawLine(
                    stepSegmentLineColor,
                    Offset(0f, 0f),
                    Offset(size.width - stepLineEndPadding.toPx(), 0f),
                    strokeWidth = stepSegmentLineWidth.toPx()
                )
                drawLine(
                    stepSegmentLineColor,
                    Offset(0f, size.height),
                    Offset(size.width - stepLineEndPadding.toPx(), size.height),
                    strokeWidth = stepSegmentLineWidth.toPx()
                )


                drawLine(
                    stepSegmentLineColor,
                    Offset(0f, offset),
                    Offset(size.width - stepLineEndPadding.toPx(), offset),
                    strokeWidth = stepSegmentLineWidth.toPx()
                )

                drawLine(
                    stepSegmentLineColor,
                    Offset(0f, size.height - offset),
                    Offset(size.width - stepLineEndPadding.toPx(), size.height - offset),
                    strokeWidth = stepSegmentLineWidth.toPx()
                )
            }



            pointLists.forEachIndexed { index, lineOption ->
                if (checkedState[index]) {
                    val totalYUnit = lineOption.HighUnit - lineOption.LowUnit
                    val xUnit = size.width / totalXUnit
                    val yUnit = size.height / totalYUnit
                    it.saveLayer(
                        Rect(
                            Offset.Zero, size
                        ), paint = Paint()
                    )
                    val pointList = lineOption.points
                    val path = Path()
                    if (pointList.size > 0) {
                        path.moveTo(
                            0f, (totalYUnit - (pointList.get(0).y - lineOption.LowUnit)) * yUnit
                        )
                    }
                    for (index in 0 until pointList.size - 1) {
                        if (pointList[index + 1].y != -1f) {
                            val x3 = (index + 1) * 0.1f * totalXUnit * xUnit
                            val y3 =
                                (totalYUnit - (pointList[index + 1].y - lineOption.LowUnit)) * yUnit
                            if (pointList.size > 1 && pointList.get(index).y == -1f) {
                                path.moveTo(
                                    x3, y3
                                )
                            } else {
                                val x1 =
                                    (((index + 1) * 0.1f * totalXUnit + index * 0.1f * totalXUnit) / 2) * xUnit
                                val y1 =
                                    (totalYUnit - (pointList[index].y - lineOption.LowUnit)) * yUnit
                                val y2 =
                                    (totalYUnit - (pointList[index + 1].y - lineOption.LowUnit)) * yUnit

                                path.cubicTo(x1, y1, x1, y2, x3, y3)
                            }
                        }
                    }
                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(path = path, false)
                    if (pointList.size > 11) {
                        path.translate(
                            Offset(
                                -0.1f * pathLength * totalXUnit * xUnit, 0f
                            )
                        )
                    } else {
                        path.translate(
                            Offset(
                                (10 - pointList.size + 2) / 10f * totalXUnit * xUnit - 0.1f * pathLength * totalXUnit * xUnit,
                                0f
                            )
                        )
                    }
                    drawPath(
                        path, color = lineOption.color, style = Stroke(curveLineWidth.toPx())
                    )
                    it.restore()
                }
            }

        }
    })
}
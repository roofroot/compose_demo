package com.compose.demo.widget

import android.graphics.Point
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.parseHeader
import com.compose.demo.ui.page.getRandomColor
import com.compose.demo.ui.theme.ghostwhite
import com.compose.demo.ui.theme.lightcyan
import com.compose.demo.ui.theme.mintcream
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random


/**
 *
 * @property color Color 指定范围曲线的颜色
 * @property bgColor Color 指定范围的背景色
 * @property startUnit Int 指定范围的开始刻度
 * @property endUnit Int 指定范围的结束刻度
 * @constructor
 */
data class LineOption(val points: List<Point>, val color: Color)


/**
 * @param modifier Modifier 图表的modifier，可以通过这个modifier调整整个图表的大小等
 * @param pointList ArrayList<Point> 图表的数据
 * @param totalXUnit Int X轴的总单位数量
 * @param totalYUnit Int Y轴的总单位数量
 * @param minUnit Float 最小单位，例如0.1，totalUnit是1000，则代表整个轴被等分为1000个单位，每个单位代表0.1
 * @param decimalFormat DecimalFormat 当最小单位是小数时，可以通过这个参数指定小数显示的格式化，默认保留两位
 * @param xMarkLine List<MarkLine>?  x轴上的刻度，具体参数查看MarkLine
 *
 * @param yMarkLine List<MarkLine>? y轴上的刻度
 * @param stepColorList List<StepColor>? 可以分段显示纵轴的刻度线。具体参数查看StepColor
 * @param showMoveMarkLine Boolean 是否显示可移动的标记线
 * @param showBaseLine Boolean 是否显示X轴，Y轴基准线
 * @param showStepSegmentLine Boolean 如果指定了stepColorList的情况，是否显示分段的间隔线、
 * @param stepSegmentLineColor Color 如果指定了stepColorList的情况，设置了显示分段的分割线，可以指定分割线颜色
 * @param stepSegmentLineWidth Dp 如果指定了stepColorList的情况，设置了显示分段的分割线，可以指定分割线宽度
 * @param showStepBg Boolean 如果指定了stepColorList的情况，是否展示背景色
 * @param dotColor Color 数据点的颜色
 * @param curveLineColor Color 曲线的颜色
 * @param baselineColor Color 基准线的颜色
 * @param moveXLineColor Color 可移动标记线X轴颜色
 * @param moveYLineColor Color 可移动标记下Y轴颜色
 * @param textColor Color 指可移动标记线附件显示的标记线当前所在位置的刻度的文字的颜色
 * @param moveLineTextSize TextUnit 指可移动标记线附件显示的标记线当前所在位置的刻度的文字大小
 * @param dotSize Dp 数据点的大小
 * @param curveLineWidth Dp 曲线的宽度
 * @param baseLineWidth Dp 基准线的宽度
 * @param moveLineWidth Dp 可移动标记线的宽度
 */
@Composable
fun TestCurveChart(
    modifier: Modifier = Modifier.fillMaxSize(),
    pointLists: List<LineOption>,
    totalXUnit: Int,
    totalYUnit: Int,
    showBaseLine: Boolean = false,
    showStepSegmentLine: Boolean = true,
    stepColorList: List<StepColor>? = null,
    stepSegmentLineColor: Color = Color.LightGray,
    stepSegmentLineWidth: Dp = 0.5.dp,
    baselineColor: Color = Color.Black,
    curveLineWidth: Dp = 1.dp,
    baseLineWidth: Dp = 1.dp,
    pathLength: Float = 1f
) {
//    val infiniteTransition = rememberInfiniteTransition(label = "loading transition")
//    val rotateAnimation by infiniteTransition.animateFloat(
//        initialValue = pathLength,
//        targetValue = -1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(
//                durationMillis = 1000,
//                easing = LinearEasing
//            )
//        ), label = "loading animation"
//    )
    val animOffset = animateFloatAsState(targetValue = pathLength)
    Canvas(modifier, onDraw = {
        drawIntoCanvas {
            val xUnit = size.width / totalXUnit
            val yUnit = size.height / totalYUnit
            stepColorList?.forEach {
//                    if (showStepBg) {
//                        drawRect(
//                            color = it.bgColor,
//                            Offset(0f, (totalYUnit - it.endUnit) * yUnit),
//                            Size(size.width, (it.endUnit - it.startUnit) * yUnit),
//                        )
//                    }
                if (it.startUnit > 0 && showStepSegmentLine) {
                    drawLine(
                        stepSegmentLineColor,
                        Offset(0f, (totalYUnit - it.startUnit) * yUnit),
                        Offset(size.width, (totalYUnit - it.startUnit) * yUnit),
                        strokeWidth = stepSegmentLineWidth.toPx()
                    )
                }
            }
            it.saveLayer(
                Rect(
                    Offset.Zero, size
                ), paint = Paint()
            )
            pointLists.forEach { lineOption ->
                val pointList = lineOption.points
                val path = Path()
                if (pointList.size > 0) {
                    path.moveTo(
                        0f, (totalYUnit - pointList.get(0).y) * yUnit
                    )
                }
                for (index in 0 until pointList.size - 1) {
                    val x1 =
                        (((index + 1) * 0.1f * totalXUnit + index * 0.1f * totalXUnit) / 2) * xUnit
                    val y1 = (totalYUnit - pointList[index].y) * yUnit
                    val y2 = (totalYUnit - pointList[index + 1].y) * yUnit
                    val x3 = (index + 1) * 0.1f * totalXUnit * xUnit
                    val y3 = (totalYUnit - pointList[index + 1].y) * yUnit
                    path.cubicTo(x1, y1, x1, y2, x3, y3)
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
            }
            it.restore()

            if (showBaseLine) {
                drawLine(
                    baselineColor,
                    Offset(0f, 0f),
                    Offset(0f, size.height),
                    strokeWidth = baseLineWidth.toPx()
                )
                drawLine(
                    baselineColor,
                    Offset(0f, size.height),
                    Offset(size.width, size.height),
                    strokeWidth = baseLineWidth.toPx()
                )
            }
        }
    })
}

@Preview
@Composable
fun TestChart() {
//    val dotColor = getRandomColor()
//    val baselineColor = getRandomColor()
//    val xLineColor = getRandomColor()
//    val yLineColor = getRandomColor()
//    val textColor = getRandomColor()
//    val curveColor = getRandomColor()
//    val lineColors= listOf<Color>(Color.Blue, Color.Red,Color.Green,Color.Cyan,Color.Yellow,Color.Magenta)
    val lines = remember {
        mutableStateListOf<LineOption>()
    }
    val pathOffset = remember {
        mutableStateOf(1f)
    }
    val time = remember {
        mutableStateOf(-1L)
    }
    var start = remember {
        mutableStateOf(false)
    }
    var data = remember {
        mutableStateListOf(0)
    }
    LaunchedEffect(key1 = Unit) {
        for (i in 0..6) {
            data.add(Random.nextInt(i * 100, i * 100 + 200))
        }
        while (true) {
            for (i in 0..6) {
                data.set(i, Random.nextInt(i * 100, i * 100 + 200))
            }
            start.value = true
            delay(1000)
        }
    }
    LaunchedEffect(Unit) {
        while (start.value == false) {
            delay(1000)
        }
        for (i in 0..6) {
            val list = ArrayList<Point>()
//            var a = 0
//            while (a <1000) {
//                list.add(Point(a, Random.nextInt(i * 100, i * 100 + 200)))
//                a += 100
//            }
            val lineOption = LineOption(list, getRandomColor())
            lines.add(lineOption)
        }

        while (true) {
            if (pathOffset.value >= 1f) {
                Log.e("time", "time:${System.currentTimeMillis() - time.value}")
                time.value = System.currentTimeMillis()
                val points = ArrayList<LineOption>()
                for (i in 0..6) {
                    val list = lines.get(i).points.toMutableList()
                    if (list.size > 11) {
                        list.removeAt(0)
                    }
                    list.add(Point(1000, data.get(i)))
                    val lineOption = LineOption(list, lines.get(i).color)
                    points.add(lineOption)
                }
                lines.clear()
                lines.addAll(points)
                pathOffset.value = 0f

                while (pathOffset.value < 1f) {
                    delay(19)
                    pathOffset.value =
                        if ((pathOffset.value + 0.02f) > 1f) 1f else (pathOffset.value + 0.02f)
                }
//                if (start.value == false) {
//                    scope.launch {
//                        start.value = true
//
//                        start.value = false
//                        Log.e("time", "end")
//                    }
//                }
            }
        }

    }


    val stepColorList = listOf(
        StepColor(getRandomColor(), lightcyan, 0, 300),
        StepColor(getRandomColor(), ghostwhite, 300, 600),
        StepColor(
            getRandomColor(), mintcream, 600, 1000
        )
    )


    TestCurveChart(
        pointLists = lines,
        totalXUnit = 1000,
        totalYUnit = 1000,
        stepColorList = stepColorList,
        curveLineWidth = 5.dp,
        pathLength = pathOffset.value
    )
}
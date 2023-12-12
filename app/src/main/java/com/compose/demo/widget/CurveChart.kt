package com.compose.demo.widget

import android.graphics.Point
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @property color Color 刻度线的颜色
 * @property unit Int 刻度线最小间隔是多少个单位
 * @property height Dp 刻度线高度
 * @property width Dp 刻度线的宽度
 * @property showUnit Boolean 是否在刻度线位置显示刻度线指定的单位
 * @property textColor Color 如果显示刻度单位，文字可以指定颜色
 * @property textFormat String 可以指定显示的刻度单位的格式化，默认只显示单位的数值，可以指定“%scm”等等
 * @property textSize TextUnit 可以指定显示的刻度单位的文字大小
 * @constructor
 */
data class MarkLine(
    val color: Color,
    val unit: Int,
    val height: Dp,
    val width: Dp,
    val showUnit: Boolean,
    val textColor: Color = Color.Black,
    val textFormat: String = "%s",
    val textSize: TextUnit = 8.sp
)

/**
 *
 * @property color Color 指定范围曲线的颜色
 * @property bgColor Color 指定范围的背景色
 * @property startUnit Int 指定范围的开始刻度
 * @property endUnit Int 指定范围的结束刻度
 * @constructor
 */
data class StepColor(val color: Color, val bgColor: Color, val startUnit: Int, val endUnit: Int)

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
fun CurveChart(
    modifier: Modifier = Modifier.fillMaxSize(),
    pointList: ArrayList<Point>,
    totalXUnit: Int,
    totalYUnit: Int,
    minUnit: Float = 1f,
    decimalFormat: DecimalFormat = DecimalFormat("0.##"),
    xMarkLine: List<MarkLine>? = null,
    yMarkLine: List<MarkLine>? = null,
    stepColorList: List<StepColor>? = null,
    showMoveMarkLine: Boolean = true,
    showBaseLine: Boolean = true,
    showStepSegmentLine: Boolean = true,
    stepSegmentLineColor: Color = Color.LightGray,
    stepSegmentLineWidth: Dp = 0.5.dp,
    showStepBg: Boolean = true,
    dotColor: Color = Color.Blue,
    curveLineColor: Color = Color.Blue,
    baselineColor: Color = Color.Black,
    moveXLineColor: Color = Color.Black,
    moveYLineColor: Color = Color.Black,
    textColor: Color = Color.Black,
    moveLineTextSize: TextUnit = 14.sp,
    dotSize: Dp = 3.dp,
    curveLineWidth: Dp = 1.dp,
    baseLineWidth: Dp = 1.dp,
    moveLineWidth: Dp = 0.5.dp
) {


    val mTextMeasurer = rememberTextMeasurer()

    val lineX = remember {
        mutableStateOf(0f)
    }
    val lineY = remember {
        mutableStateOf(0f)
    }
    Canvas(
        modifier
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = { offset ->
                    lineX.value = offset.x
                    lineY.value = offset.y
                }) { pointerInputChange: PointerInputChange, offset: Offset ->
                    if (lineX.value + offset.x >= 0 && lineX.value + offset.x <= this.size.width) {
                        lineX.value += offset.x
                    }
                    if (lineY.value + offset.y >= 0 && lineY.value + offset.y <= this.size.height) {
                        lineY.value += offset.y
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    val xUnit = size.width.toFloat() / totalXUnit
                    val yUnit = size.height.toFloat() / totalYUnit
                    val list = ArrayList<Point>()
                    pointList.forEach {
                        if (abs((offset.x / xUnit).roundToInt() - it.x) < (totalXUnit / 100) * 2 && abs(
                                (totalYUnit - offset.y / yUnit).roundToInt() - it.y
                            ) < (totalYUnit / 100) * 2
                        ) {
                            list.add(it)
                            Log.e("aaaaaaaaaaaaaa", "(${it.x},${it.y})")
                        }
                    }
                    if (list.size > 0) {
                        if (list.size == 1) {
                            lineX.value = (list[0].x * xUnit)
                            lineY.value = (((totalYUnit - list[0].y)) * yUnit)
                        } else {
                            var tempIndex = 0
                            var temp = abs((offset.x / xUnit).roundToInt() - list[0].x)
                            list.forEachIndexed { index, point ->
                                if (abs((offset.x / xUnit).roundToInt() - point.x) < temp) {
                                    temp = abs((offset.x / xUnit).roundToInt() - point.x)
                                    tempIndex = index
                                }
                            }
                            lineX.value = (list[tempIndex].x * xUnit)
                            lineY.value = (((totalYUnit - list[tempIndex].y)) * yUnit)
                        }
                    }
                })
            }, onDraw = {
            drawIntoCanvas {
                val xUnit = size.width / totalXUnit
                val yUnit = size.height / totalYUnit
                stepColorList?.forEach {
                    if (showStepBg) {
                        drawRect(
                            color = it.bgColor,
                            Offset(0f, (totalYUnit - it.endUnit) * yUnit),
                            Size(size.width, (it.endUnit - it.startUnit) * yUnit),
                        )
                    }
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
                val path = Path()
                path.moveTo(pointList.get(0).x * xUnit, (totalYUnit - pointList.get(0).y) * yUnit)
                for (index in 0 until pointList.size - 1) {
                    val x1 = ((pointList[index + 1].x + pointList[index].x) / 2) * xUnit
                    val y1 = (totalYUnit - pointList[index].y) * yUnit
                    val y2 = (totalYUnit - pointList[index + 1].y) * yUnit
                    val x3 = pointList[index + 1].x * xUnit
                    val y3 = (totalYUnit - pointList[index + 1].y) * yUnit
                    path.cubicTo(x1, y1, x1, y2, x3, y3)
                }
                drawPath(path, color = curveLineColor, style = Stroke(curveLineWidth.toPx()))
                stepColorList?.forEach {
                    drawRect(
                        color = it.color,
                        Offset(0f, (totalYUnit - it.endUnit) * yUnit),
                        Size(size.width, (it.endUnit - it.startUnit) * yUnit),
                        blendMode = BlendMode.SrcAtop
                    )
                }
                it.restore()
                if (showMoveMarkLine) {
                    drawLine(
                        moveXLineColor,
                        Offset(lineX.value, 0f),
                        Offset(lineX.value, size.height),
                        strokeWidth = moveLineWidth.toPx()
                    )

                    drawLine(
                        moveYLineColor,
                        Offset(0f, lineY.value),
                        Offset(size.width, lineY.value),
                        strokeWidth = moveLineWidth.toPx()
                    )

                    val textStr =
                        "(x:${decimalFormat.format((lineX.value / xUnit).roundToInt() * minUnit)},y:${
                            decimalFormat.format(
                                (totalYUnit - (lineY.value / yUnit)).roundToInt() * minUnit
                            )
                        })"
                    val layout = mTextMeasurer.measure(
                        text = textStr,
                        style = TextStyle(fontSize = moveLineTextSize),
                    )
                    var offsetX = 0
                    var offsetY = 0
                    if (layout.size.width > size.width - lineX.value) {
                        offsetX = -layout.size.width
                    }
                    if (layout.size.height > size.height - lineY.value) {
                        offsetY = -layout.size.height
                    }
                    drawText(
                        textMeasurer = mTextMeasurer,
                        text = textStr,
                        topLeft = Offset(lineX.value + offsetX, lineY.value + offsetY),
                        style = TextStyle(fontSize = moveLineTextSize, color = textColor)
                    )
                }
                xMarkLine?.forEach {
                    for (i in 0..totalXUnit step it.unit) {
                        drawLine(
                            it.color,
                            Offset(i * xUnit, size.height),
                            Offset(i * xUnit, size.height - it.height.toPx()),
                            strokeWidth = it.width.toPx()
                        )
                        if (it.showUnit) {
                            val unitFormat =
                                String.format(it.textFormat, decimalFormat.format(i * minUnit))
                            val layout = mTextMeasurer.measure(
                                text = unitFormat,
                                style = TextStyle(fontSize = it.textSize),
                                constraints = Constraints(maxWidth = size.width.toInt())
                            )
                            if (i != totalXUnit && i != 0) {
                                drawText(
                                    textMeasurer = mTextMeasurer,
                                    text = unitFormat,
                                    topLeft = Offset(
                                        i * xUnit - layout.size.width / 2,
                                        size.height - it.height.toPx() - layout.size.height
                                    ),
                                    style = TextStyle(
                                        fontSize = it.textSize, color = it.textColor
                                    )
                                )
                            }
                        }
                    }

                }
                yMarkLine?.forEach {
                    for (i in 0 until totalYUnit step it.unit) {

                        drawLine(
                            it.color,
                            Offset(0f, i * yUnit),
                            Offset(it.height.toPx(), i * yUnit),
                            strokeWidth = it.width.toPx()
                        )
                        if (it.showUnit) {
                            val unitFormat = String.format(
                                it.textFormat,
                                decimalFormat.format((totalYUnit - i) * minUnit)
                            )
                            val layout = mTextMeasurer.measure(
                                text = unitFormat,
                                style = TextStyle(fontSize = it.textSize),
                                constraints = Constraints(maxWidth = size.width.toInt())
                            )
                            if (i != totalXUnit && i != 0) {
                                drawText(
                                    textMeasurer = mTextMeasurer,
                                    text = unitFormat,
                                    topLeft = Offset(
                                        it.height.toPx(), i * yUnit - layout.size.height / 2
                                    ),
                                    style = TextStyle(
                                        fontSize = it.textSize, color = it.textColor
                                    )
                                )
                            }
                        }
                    }

                }
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

                for (index in 0 until pointList.size) {
                    val x = pointList[index].x * xUnit - dotSize.toPx() / 2
                    val y = ((totalYUnit - pointList[index].y)) * yUnit - dotSize.toPx() / 2
                    drawOval(dotColor, Offset(x, y), Size(dotSize.toPx(), dotSize.toPx()))
                }
            }


        })
}
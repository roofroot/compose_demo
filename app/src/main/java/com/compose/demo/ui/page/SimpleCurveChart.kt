package com.compose.demo.ui.page

import android.graphics.Point
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compose.demo.ui.theme.ghostwhite
import com.compose.demo.ui.theme.lightcyan
import com.compose.demo.ui.theme.mintcream
import com.compose.demo.widget.CurveChart
import com.compose.demo.widget.MarkLine
import com.compose.demo.widget.StepColor
import kotlin.random.Random

@Composable
fun SimpleCurveChart() {
    val list = ArrayList<Point>()
    var a = 0
    while (a < 100) {
        list.add(Point(a, Random.nextInt(0, 100)))
        a += Random.nextInt(1, 10)
    }

    val xMarkLine = listOf<MarkLine>(
        MarkLine(Color.LightGray, 1, 5.dp, 0.5.dp, false),
        MarkLine(Color.Gray, 5, 10.dp, 0.8.dp, false),
        MarkLine(Color.Black, 10, 15.dp, 1.dp, true)

    )
    val yMarkLine = listOf<MarkLine>(
        MarkLine(Color.LightGray, 1, 5.dp, 0.5.dp, false),
        MarkLine(Color.Gray, 5, 10.dp, 0.8.dp, false),
        MarkLine(Color.Black, 10, 15.dp, 1.dp, true)
    )
    val stepColorList = listOf(
        StepColor(getRandomColor(), lightcyan, 0, 30),
        StepColor(getRandomColor(), ghostwhite, 30, 60), StepColor(
            getRandomColor(), mintcream,
            60, 100
        )
    )

    val dotColor = getRandomColor()
    val baselineColor = getRandomColor()
    val xLineColor = getRandomColor()
    val yLineColor = getRandomColor()
    val textColor = getRandomColor()
    val curveColor = getRandomColor()
    CurveChart(
        pointList = list,
        totalXUnit = 100,
        totalYUnit = 100,
        xMarkLine = xMarkLine,
        yMarkLine = yMarkLine,
        stepColorList = stepColorList,
        dotColor = dotColor,
        dotSize = 5.dp,
        baselineColor = baselineColor,
        moveXLineColor = xLineColor,
        moveYLineColor = yLineColor,
        textColor = textColor,
        curveLineColor = curveColor
    )
}
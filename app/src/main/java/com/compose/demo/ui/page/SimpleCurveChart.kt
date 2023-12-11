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
    while (a < 1500) {
        list.add(Point(a, Random.nextInt(0, 1000)))
        a += Random.nextInt(1, 100)
    }

    val xMarkLine = listOf<MarkLine>(
        MarkLine(Color.LightGray, 10, 5.dp, 0.5.dp, false),
        MarkLine(Color.Gray, 50, 10.dp, 0.8.dp, false),
        MarkLine(Color.Black, 100, 15.dp, 1.dp, true)

    )
    val yMarkLine = listOf<MarkLine>(
        MarkLine(Color.LightGray, 10, 5.dp, 0.5.dp, false),
        MarkLine(Color.Gray, 50, 10.dp, 0.8.dp, false),
        MarkLine(Color.Black, 100, 15.dp, 1.dp, true)
    )
    val stepColorList = listOf(
        StepColor(getRandomColor(), lightcyan, 0, 300),
        StepColor(getRandomColor(), ghostwhite, 300, 600), StepColor(
            getRandomColor(), mintcream,
            600, 1000
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
        totalXUnit = 1500,
        totalYUnit = 1000,
        xMarkLine = xMarkLine,
        yMarkLine = yMarkLine,
        stepColorList = stepColorList,
        dotColor = dotColor,
        dotSize = 5.dp,
        baselineColor = baselineColor,
        moveXLineColor = xLineColor,
        moveYLineColor = yLineColor,
        textColor = textColor,
        curveLineColor = curveColor,
        minUnit = 0.1f
    )
}
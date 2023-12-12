package com.compose.demo.widget

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.parseWidget
import kotlin.math.roundToInt

@Composable
fun CircleProgress(
    modifier: Modifier = Modifier, bgColor: Color = Color.Gray, fgColor: Color = Color.Blue,
    currentProgress: MutableState<Int>,
    totalProgress: Int = 100,
    progressBarWidth: Dp = 20.dp,
    colorList: List<Color>? = null,
    angleList: List<Float>? = null
) {

    var angelProgress = remember {
        mutableStateOf(currentProgress.value.toFloat() / totalProgress * 360f)
    }
    var progressChange =
        Math.abs(currentProgress.value.toFloat() / totalProgress * 360f - angelProgress.value)
            .roundToInt()
    angelProgress.value = currentProgress.value.toFloat() / totalProgress * 360f

    val animateProgress =
        animateFloatAsState(
            targetValue = angelProgress.value,
            animationSpec = TweenSpec(durationMillis = progressChange * 10)
        )
    Canvas(modifier = modifier, onDraw = {
        drawIntoCanvas { canvas ->
            var width = size.width
            var height = size.height
            if (width > height) {
                width = height
            } else {
                height = width
            }
            canvas.saveLayer(
                Rect(
                    Offset.Zero, size
                ), paint = Paint()
            )
            var rect = Rect(Offset.Zero, Size(width, height))
            val paint = Paint()
            paint.color = bgColor
            canvas.drawCircle(Offset(width / 2, height / 2), width / 2, paint)
            paint.color = fgColor
            canvas.saveLayer(
                Rect(
                    Offset.Zero, size
                ), paint = Paint()
            )
            canvas.drawArc(rect, -90f, animateProgress.value, true, paint)
            if (colorList != null && angleList != null && colorList.size == angleList.size) {
                angleList.reversed().forEachIndexed { index, fl ->
                    paint.color = colorList.reversed()[index]
                    paint.blendMode = BlendMode.SrcAtop
                    canvas.drawArc(rect, -90f, fl, true, paint)
                }
            }
            canvas.restore()

            paint.blendMode = BlendMode.Clear
            canvas.drawCircle(
                Offset(width / 2, height / 2),
                width / 2 - progressBarWidth.toPx(),
                paint
            )
            canvas.restore()
        }
    })
}
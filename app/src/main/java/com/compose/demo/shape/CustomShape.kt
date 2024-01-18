package com.compose.demo.shape

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

//五角星
class pentagramShape(
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        Log.e("size", "width${size.width}")
        val starCount = 5
        val radiusOuter = if (size.width > size.height) size.height / 2 else size.width / 2 //五角星外圆径
        val radiusInner = radiusOuter / 2 //五角星内圆半径
        val startAngle = (-Math.PI / 2).toFloat() //开始绘制点的外径角度
        val perAngle = (2 * Math.PI / starCount).toFloat() //两个五角星两个角直接的角度差
        val outAngles = (0 until starCount).map {
            val angle = it * perAngle + startAngle
            Offset(radiusOuter * cos(angle), radiusOuter * sin(angle))
        }//所有外圆角的顶点
        val innerAngles = (0 until starCount).map {
            val angle = it * perAngle + perAngle / 2 + startAngle
            Offset(radiusInner * cos(angle), radiusInner * sin(angle))
        }//所有内圆角的顶点
        val path = Path()//绘制五角星的所有内圆外圆的点连接线
        (0 until starCount).forEachIndexed { index, _ ->
            val outerX = outAngles[index].x
            val outerY = outAngles[index].y
            val innerX = innerAngles[index].x
            val innerY = innerAngles[index].y
            if (index == 0) {
                path.moveTo(outerX, outerY)
                path.lineTo(innerX, innerY)
                path.lineTo(
                    outAngles[(index + 1) % starCount].x,
                    outAngles[(index + 1) % starCount].y
                )
            } else {
                path.lineTo(innerX, innerY)//移动到内圆角的端点
                path.lineTo(
                    outAngles[(index + 1) % starCount].x,
                    outAngles[(index + 1) % starCount].y
                )//连接到下一个外圆角的端点
            }
            if (index == starCount - 1) {
                path.close()
            }
        }
        path.translate(Offset(size.width / 2, size.height / 2))
        return Outline.Generic(path)
    }
}

/**
 * @param waveWidth 波浪宽度
 * @param waveHeight 波浪高度（并不是实际的高度，是贝塞尔曲线的控制点距离起始点的高度）
 * @param roundFixed 默认为true,会根据实际的组件大小和波浪的宽度取近似值，让波浪可以平均分布
 */
class WaveBorderShape(
    waveWidth: Dp,
    waveHeight: Dp,
    roundFixed: Boolean = true,
) : Shape {
    val waveWidth = waveWidth
    val waveHeight = waveHeight
    val roundFixed = roundFixed
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        var waveWidthPx = (waveWidth.value * density.density)
        val waveHeightPx = (waveHeight.value * density.density)
        var left = waveHeightPx;
        var path = Path();
        path.moveTo(waveHeightPx, waveHeightPx)
        if (roundFixed) {
            val a = ((size.width - waveHeightPx * 2) / waveWidthPx).roundToInt()
            waveWidthPx = (size.width - waveHeightPx * 2) / a
        }
        while (left < size.width - (waveHeightPx + waveWidthPx)) {
            var rect = Rect(
                left, 0f, left + waveWidthPx, waveHeightPx
            )
            path.quadraticBezierTo(
                rect.left + rect.size.width / 2, rect.top,
                rect.right, rect.bottom
            )
            left += waveWidthPx
        }
        var rect = Rect(
            left, 0f, size.width - waveHeightPx, waveHeightPx
        )
        path.quadraticBezierTo(
            rect.left + rect.size.width / 2, rect.top,
            rect.right, rect.bottom
        )
        if (roundFixed) {
            val a = ((size.height - waveHeightPx * 2) / waveWidthPx).roundToInt()
            waveWidthPx = (size.height - waveHeightPx * 2) / a
        }

        var top = waveHeightPx
        while (top < size.height - (waveHeightPx + waveWidthPx)) {
            var rect = Rect(
                size.width - waveHeightPx, top, size.width, top + waveWidthPx
            )
            path.quadraticBezierTo(
                rect.right, rect.top + rect.size.height / 2,
                rect.left, rect.bottom
            )
            top += waveWidthPx
        }
        rect = Rect(
            size.width - waveHeightPx, top, size.width, size.height - waveHeightPx
        )
        path.quadraticBezierTo(
            rect.right, rect.top + rect.size.height / 2,
            rect.left, rect.bottom
        )
        if (roundFixed) {
            val a = ((size.width - waveHeightPx * 2) / waveWidthPx).roundToInt()
            waveWidthPx = (size.width - waveHeightPx * 2) / a
        }
        var right = size.width - waveHeightPx
        while (right > (waveHeightPx + waveWidthPx)) {
            var rect = Rect(
                right - waveWidthPx, size.height - waveHeightPx, right, size.height
            )
            path.quadraticBezierTo(
                rect.right - rect.size.width / 2, rect.bottom,
                rect.left, rect.top
            )
            right -= waveWidthPx
        }
        rect = Rect(
            waveHeightPx, size.height - waveHeightPx, right, size.height
        )
        path.quadraticBezierTo(
            rect.right - rect.size.width / 2, rect.bottom,
            rect.left, rect.top
        )
        if (roundFixed) {
            val a = ((size.height - waveHeightPx * 2) / waveWidthPx).roundToInt()
            waveWidthPx = (size.height - waveHeightPx * 2) / a
        }
        var bottom = size.height - waveHeightPx
        while (bottom > (waveHeightPx + waveWidthPx)) {
            var rect = Rect(
                0f, bottom - waveWidthPx, waveHeightPx, bottom
            )
            path.quadraticBezierTo(
                rect.left, rect.top + rect.size.height / 2,
                rect.right, rect.top
            )
            bottom -= waveWidthPx
        }
        rect = Rect(
            0f, waveHeightPx, waveHeightPx, bottom
        )
        path.quadraticBezierTo(
            rect.left, rect.top + rect.size.height / 2,
            rect.right, rect.top
        )
        return Outline.Generic(path)
    }
}

/**
 * @param waveCount 如果制定了waveCount,将根据waveCount计算waveWidth
 * @param waveWidth 波浪宽度
 * @param waveHeight 波浪高度（并不是实际的高度，是贝塞尔曲线的控制点距离起始点的高度）
 */
class FlowerShape(
    waveCount: Int = -1,
    waveWidth: Dp,
    waveHeight: Dp,
) : Shape {
    val waveWidth = waveWidth
    val waveHeight = waveHeight
    val waveCount = waveCount
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val waveWidthPx = (waveWidth.value * density.density)
        val waveHeightPx = (waveHeight.value * density.density)
        val x0 = size.width / 2
        val y0 = size.height / 2
        var br = 0f
        if (size.width > size.height) {
            br = size.height / 2
        } else {
            br = size.width / 2
        }
        val sr = br - waveHeightPx

        val c = (2 * Math.PI * sr)
        var count = 0
        if (waveCount == -1) {
            count = ((c / waveWidthPx).roundToInt()) + 1
        } else {
            count = waveCount
        }
        val angleStep = 360.toDouble() / count
        var angle = 0.0
        val startX = (x0 + sr * cos(Math.toRadians(angle))).toFloat()
        val startY = (y0 + sr * sin(Math.toRadians(angle))).toFloat()
        var path = Path();
        path.moveTo(startX, startY)
        Log.e("aaa", angle.toString() + "," + angleStep)
        while (true) {
            angle += angleStep
            var sRadians = Math.toRadians(angle)
            if (angle > 360) {
                angle = 360.0
            }
            var bRadians = Math.toRadians(angle - angleStep / 2)
            val sx = (x0 + sr * cos(sRadians)).toFloat()
            val sy = (y0 + sr * sin(sRadians)).toFloat()
            val bx = (x0 + br * cos(bRadians)).toFloat()
            val by = (y0 + br * sin(bRadians)).toFloat()
            path.quadraticBezierTo(bx, by, sx, sy)
            if (angle == 360.0) {
                break
            }
        }
        return Outline.Generic(path)
    }
}

class TriangleShape(
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        var rect = Rect(
            0f, 0f, size.width, size.height
        )
        var path = Path();
        path.moveTo(rect.left, rect.top)
        path.lineTo(rect.right, rect.top)
        path.lineTo(rect.size.width / 2, rect.bottom)
        path.close()
        return Outline.Generic(path)
    }
}

class RectShape(
    rect: Rect?
) : Shape {
    val rect: Rect? = rect
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        var path = Path();
        rect?.let {
            path.addRect(rect)
            path.close()
        }
        return Outline.Generic(path)
    }
}

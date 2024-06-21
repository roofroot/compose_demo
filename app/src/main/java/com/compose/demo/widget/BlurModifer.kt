package com.compose.demo.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun getBlurState(): BlurState {
    val blurState = BlurState()
    blurState.childRectMap = remember {
        mutableStateMapOf()
    }
    blurState.childCustomOption = remember {
        mutableStateMapOf()
    }
    return blurState
}

class BlurState {
    lateinit var childRectMap: MutableMap<String, Rect?>
    lateinit var childCustomOption: MutableMap<String, CustomOption?>
}

/**
 * shapeType 0，1 , 2 , 矩形，圆形，path自定义图形
 * rectRadiusX,Y  如果类型是0，可以设置矩形圆角，实际的圆角大小为实际控件宽高的百分比，例如rectRadiusX设置为0.1实际宽度是100，则在水平方向的圆角是10
 * path,如果设置了图形的type是2, 需要传入对应的path
 * pathsize，不会影响实际的渲染大小，实际渲染大小会根据实际测量的尺寸，将这个path按照比例放大，如果path是固定的大小，应当设置为path绘制的实际大小
 */
data class CustomOption(
    val shapeType: Int = 0,
    val rectRadiusX: Float = 0f,
    val rectRadiusY: Float = 0f,
    val path: android.graphics.Path? = null,
    val pathSize: Size = Size(100f, 100f),
    val colorBlend: Int = Color.White.toArgb()
)

/**
 *
 * @receiver Modifier
 * @param key String 多个控件需要渲染毛玻璃效果时用于区分
 * @param state BlurState
 * @param option CustomOption? 自定义的参数
 * @return Modifier
 */

fun Modifier.customChildBlur(
    key: String, state: BlurState, option: CustomOption? = null
): Modifier {
    state.childCustomOption[key] = option
    return this.then(onPlaced {
        state.childRectMap[key] = it.boundsInParent()
    })
}


@RequiresApi(Build.VERSION_CODES.S)
fun Modifier.customParentBlur(blur: Float, blurState: BlurState) = this.then(graphicsLayer {
    var offsetResult = RenderEffect.createOffsetEffect(0f, 0f)
    blurState.childRectMap.forEach { key, rect ->
        rect?.let { rect ->
            val bitmap: Bitmap
            var bitmapShapeRender: RenderEffect? = null
            val option = blurState.childCustomOption[key]
            if (option != null) {
                when (option.shapeType) {
                    0 -> {
                        bitmap = Bitmap.createBitmap(
                            rect.width.toInt() + (option.rectRadiusX * rect.width * 2).toInt(),
                            rect.height.toInt() + (option.rectRadiusY * rect.height * 2).toInt(),
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        val paint = android.graphics.Paint()
                        paint.color = option.colorBlend
                        canvas.drawRoundRect(
                            android.graphics.RectF(
                                0f,
                                0f,
                                bitmap.width.toFloat(),
                                bitmap.height.toFloat()
                            ),
                            option.rectRadiusX * rect.width,
                            option.rectRadiusY * rect.height,
                            paint
                        )
                        bitmapShapeRender = RenderEffect.createBitmapEffect(
                            bitmap,
                            android.graphics.Rect(0, 0, bitmap.width, bitmap.height),
                            android.graphics.Rect(
                                rect.left.toInt(),
                                rect.top.toInt(),
                                rect.right.toInt(),
                                rect.bottom.toInt()
                            )
                        )
                        bitmap.recycle()
                    }

                    1 -> {
                        bitmap = Bitmap.createBitmap(
                            100, 100, Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        val paint = android.graphics.Paint()
                        paint.color = option.colorBlend
                        canvas.drawOval(
                            android.graphics.RectF(0f, 0f, 100f, 100f), paint
                        )
                        bitmapShapeRender = RenderEffect.createBitmapEffect(
                            bitmap,
                            android.graphics.Rect(0, 0, bitmap.width, bitmap.height),
                            android.graphics.Rect(
                                rect.left.toInt(),
                                rect.top.toInt(),
                                rect.right.toInt(),
                                rect.bottom.toInt()
                            )
                        )
                        bitmap.recycle()
                    }

                    2 -> {
                        bitmap = Bitmap.createBitmap(
                            option.pathSize.width.toInt(),
                            option.pathSize.height.toInt(),
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        val paint = android.graphics.Paint()
                        paint.color = option.colorBlend
                        if (option.path != null) {
                            canvas.drawPath(option.path, paint)
                        } else {
                            canvas.drawRoundRect(
                                android.graphics.RectF(
                                    0f,
                                    0f,
                                    bitmap.width.toFloat(),
                                    bitmap.height.toFloat()
                                ),
                                option.rectRadiusX * rect.width,
                                option.rectRadiusY * rect.height,
                                paint
                            )
                        }
                        bitmapShapeRender = RenderEffect.createBitmapEffect(
                            bitmap,
                            android.graphics.Rect(0, 0, bitmap.width, bitmap.height),
                            android.graphics.Rect(
                                rect.left.toInt(),
                                rect.top.toInt(),
                                rect.right.toInt(),
                                rect.bottom.toInt()
                            )
                        )
                        bitmap.recycle()
                    }


                    else -> {
                        bitmap = Bitmap.createBitmap(
                            rect.width.toInt() + (option.rectRadiusX * rect.width * 2).toInt(),
                            rect.height.toInt() + (option.rectRadiusY * rect.height * 2).toInt(),
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        val paint = android.graphics.Paint()
                        paint.color = option.colorBlend
                        canvas.drawRoundRect(
                            android.graphics.RectF(
                                0f,
                                0f,
                                bitmap.width.toFloat(),
                                bitmap.height.toFloat()
                            ),
                            option.rectRadiusX * rect.width,
                            option.rectRadiusY * rect.height,
                            paint
                        )
                        bitmapShapeRender = RenderEffect.createBitmapEffect(
                            bitmap,
                            android.graphics.Rect(0, 0, bitmap.width, bitmap.height),
                            android.graphics.Rect(
                                rect.left.toInt(),
                                rect.top.toInt(),
                                rect.right.toInt(),
                                rect.bottom.toInt()
                            )
                        )
                        bitmap.recycle()
                    }
                }

            }
            bitmapShapeRender?.let {
                val effect = RenderEffect.createBlurEffect(
                    blur,
                    blur,
                    Shader.TileMode.DECAL,
                ).asComposeRenderEffect()

                val effect2 = RenderEffect.createBlendModeEffect(
                    it, effect.asAndroidRenderEffect(), android.graphics.BlendMode.SRC_ATOP
                ).asComposeRenderEffect()
                offsetResult = RenderEffect.createBlendModeEffect(
                    effect2.asAndroidRenderEffect(),
                    offsetResult,
                    android.graphics.BlendMode.DST_ATOP
                )
            }
        }

        renderEffect = offsetResult.asComposeRenderEffect()
    }
})

fun getPentagramShapePath(
    size: Size
): android.graphics.Path {
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
    val path = android.graphics.Path()//绘制五角星的所有内圆外圆的点连接线
    (0 until starCount).forEachIndexed()
    { index, _ ->
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
    val matrix = Matrix()
    matrix.postTranslate(size.width / 2, size.height / 2)
    path.transform(matrix)
    return path

}




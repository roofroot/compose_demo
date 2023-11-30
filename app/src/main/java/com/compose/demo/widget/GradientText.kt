package com.compose.demo.widget

import android.graphics.fonts.FontStyle
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer

import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp


enum class GradientOrientation { Vertical, Horizontal, Line }

@OptIn(ExperimentalTextApi::class)
@Composable
fun GradientText(
    modifier: Modifier,
    text: String,
    maxLines: Int = Int.MAX_VALUE,
    brashColors: List<Color>? = null,
    brashTileMode: TileMode = TileMode.Clamp,
    gradientOrientation: GradientOrientation = GradientOrientation.Horizontal,
    alignment: Alignment = Alignment.Center,
    textStyle: TextStyle = TextStyle(fontSize = 15.sp, color = Color.Black),
    autoFixed: Boolean = true
) {
    val mTextMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier)
    {
        drawIntoCanvas { canvas ->
            var layout = mTextMeasurer.measure(
                text = text,
                style = textStyle,
                constraints = Constraints(maxWidth = size.width.toInt())
            )

            var mFontSize = textStyle.fontSize.value

            if ((layout.size.height > size.height || layout.lineCount > maxLines) && autoFixed) {
                while (true) {
                    mFontSize = (mFontSize - 1)
                    layout = mTextMeasurer.measure(
                        text = text,
                        style = textStyle.copy(fontSize = mFontSize.sp),
                        constraints = Constraints(maxWidth = size.width.toInt())
                    )
                    if ((layout.size.height <= size.height) && (layout.lineCount <= maxLines)) {
                        break
                    }
                }
            }

            var offsetX = ((size.width - layout.size.width) / 2)
            var offsetY = (size.height - layout.size.height) / 2
            if (alignment == Alignment.TopCenter) {
                offsetY = 0f
            } else if (alignment == Alignment.TopStart) {
                offsetY = 0f
                offsetX = 0f
            } else if (alignment == Alignment.TopEnd) {
                offsetY = 0f
                offsetX = size.width - layout.size.width
            } else if (alignment == Alignment.CenterStart) {
                offsetX = 0f
            } else if (alignment == Alignment.CenterEnd) {
                offsetX = size.width - layout.size.width
            } else if (alignment == Alignment.BottomStart) {
                offsetY = size.height - layout.size.height
                offsetX = 0f
            } else if (alignment == Alignment.BottomCenter) {
                offsetY = size.height - layout.size.height
            } else if (alignment == Alignment.BottomEnd) {
                offsetY = size.height - layout.size.height
                offsetX = size.width - layout.size.width
            }
            canvas.saveLayer(
                Rect(
                    Offset.Zero,
                    size
                ), paint = Paint()
            )
            drawText(
                textMeasurer = mTextMeasurer,
                text = text,
                maxLines = maxLines,
                topLeft = Offset(offsetX, offsetY),
                style = textStyle.copy(fontSize = mFontSize.sp)
            )
            brashColors?.let {
                var brush: Brush? = null
                if (gradientOrientation == GradientOrientation.Horizontal) {
                    brush = Brush.linearGradient(
                        start = Offset(offsetX, offsetY),
                        end = Offset(offsetX + layout.size.width, offsetY),
                        colors = it,
                        tileMode = brashTileMode
                    )
                    drawRect(
                        brush = brush!!,
                        blendMode = BlendMode.SrcIn,
                    )
                } else if (gradientOrientation == GradientOrientation.Vertical) {
                    brush = Brush.linearGradient(
                        start = Offset(offsetX + layout.size.width / 2, offsetY),
                        end = Offset(
                            offsetX + layout.size.width / 2,
                            offsetY + layout.size.height
                        ),
                        colors = it,
                        tileMode = brashTileMode
                    )
                    drawRect(
                        brush = brush!!,
                        blendMode = BlendMode.SrcIn,
                    )
                } else {
                    val list = ArrayList<Color>()
                    for (i in 0 until layout.lineCount) {
                        list.addAll(it)
                    }
                    brush = Brush.linearGradient(
                        start = Offset(offsetX + layout.size.width / 2, offsetY),
                        end = Offset(
                            offsetX + layout.size.width / 2,
                            offsetY + layout.size.height
                        ),
                        colors = list,
                        tileMode = brashTileMode
                    )
                    drawRect(
                        brush = brush!!,
                        blendMode = BlendMode.SrcIn,
                    )

                }


                canvas.restore()
            }
        }
    }
}

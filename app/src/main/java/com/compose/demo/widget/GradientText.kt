package com.compose.demo.widget

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalTextApi::class)
@Composable
fun GradientText(
    modifier: Modifier,
    text: String,
    brash: Brush,
    fontSize: TextUnit = 20.sp,
    autoFixed: Boolean = true
) {
    val mTextMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier)
    {
        drawIntoCanvas { canvas ->
            var mFontSize = fontSize
            var layout = mTextMeasurer.measure(
                text = text,
                maxLines = 1,
                style = TextStyle(fontSize = fontSize)
            )
            canvas.saveLayer(Rect(Offset.Zero, size), Paint())
            if (layout.size.width > size.width && autoFixed) {
                while (true) {
                    mFontSize = (mFontSize.value - 1).sp
                    layout = mTextMeasurer.measure(
                        text = text,
                        maxLines = 1,
                        style = TextStyle(fontSize = mFontSize)
                    )
                    if (layout.size.width < size.width && autoFixed) {
                        break
                    }
                }
            }
            drawText(
                textMeasurer = mTextMeasurer,
                text = text,
                maxLines = 1,
                topLeft = Offset(
                    if (size.width > layout.size.width)
                        ((size.width - layout.size.width) / 2) else 0f,
                    (size.height - layout.size.height) / 2
                ), style = TextStyle(fontSize = mFontSize)
            )
            // 3. 在矩形中绘制一个渐变色
            drawRect(
                brush = brash,
                blendMode = BlendMode.SrcIn
            )
            // 5. 将图层合成到 canvas 的原始图像上
            canvas.restore()
        }
    }
}
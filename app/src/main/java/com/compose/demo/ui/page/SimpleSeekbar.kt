package com.compose.demo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compose.demo.ui.theme.darkseagreen
import com.compose.demo.ui.theme.khaki
import com.compose.demo.ui.theme.lavender
import com.compose.demo.ui.theme.lightyellow
import com.compose.demo.ui.theme.mediumorchid
import com.compose.demo.ui.theme.mediumvioletred
import com.compose.demo.ui.theme.palevioletred
import com.compose.demo.ui.theme.plum
import com.compose.demo.ui.theme.thistle
import com.compose.demo.ui.theme.yellow
import com.compose.demo.widget.CustomSeekBar
import com.compose.demo.widget.CustomStepSeekBar

@Composable
fun SimpleSeekbar() {
    val progress = remember {
        mutableStateOf(0)
    }
    Column(
        Modifier.padding(top = 50.dp, start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Text("当前进度:${progress.value}%")
        CustomSeekBar(modifier = Modifier.fillMaxWidth(), currentProgress = progress)
        CustomSeekBar(
            modifier = Modifier.fillMaxWidth(),
            currentProgress = progress,
            barFgColor = plum,
            barBgColor = lavender,
            thumbColor = mediumorchid
        )
        CustomSeekBar(
            modifier = Modifier.fillMaxWidth(),
            currentProgress = progress,
            barFgColor = yellow,
            barHeight = 50.dp,
            fgOffset = 50.dp,
            barBgColor = lightyellow,
            thumbContent = {
                Box(
                    it
                        .shadow(5.dp, shape = CircleShape)
                        .width(50.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                        .background(khaki)
                ) {

                }
            }
        )
        CustomSeekBar(modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, darkseagreen, shape = RoundedCornerShape(20.dp)),
            fgOffset = 40.dp,
            barHeight = 40.dp,
            currentProgress = progress,
            barFgColor = thistle,
            barBgColor = Color.Transparent,
            thumbContent = {
                Box(
                    it
                        .shadow(5.dp, shape = CircleShape)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(darkseagreen)
                ) {

                }
            })
        val progressStep = remember {
            mutableStateOf(0)
        }
        Text("当前进度:${progressStep.value}%")
        CustomStepSeekBar(
            modifier = Modifier
                .width(800.dp),
            barHeight = 40.dp,
            currentProgress = progressStep,
            barBgColor = palevioletred,
            step = 5,
            thumbColor = mediumvioletred
        )

    }
}
package com.compose.demo.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compose.demo.ui.theme.lavender
import com.compose.demo.ui.theme.mediumorchid
import com.compose.demo.ui.theme.plum
import com.compose.demo.widget.CircleProgress
import com.compose.demo.widget.CustomSeekBar

@Composable
fun SimpleCircleProgress() {
    val currentProgress = remember {
        mutableStateOf(0)
    }
    Column {
        CircleProgress(modifier = Modifier.size(350.dp, 300.dp), currentProgress = currentProgress,
            colorList = listOf(getRandomColor(), getRandomColor(), getRandomColor()), angleList = listOf(100f,260f,360f)
        )
        CustomSeekBar(
            modifier = Modifier
                .height(50.dp)
                .width(500.dp),
            currentProgress = currentProgress,
            barFgColor = plum,
            barBgColor = lavender,
            thumbColor = mediumorchid
        )
        Button(onClick = {currentProgress.value=80}) {
            Text(text = "to %80")
        }
        Button(onClick = {currentProgress.value=20}) {
            Text(text = "to %20")
        }
    }

}
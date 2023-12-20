package com.compose.demo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compose.demo.widget.CalenderView

@Composable
fun SimpleCalendarView() {
    val selectTime = remember {
        mutableStateListOf<Long>()
    }
    Column(Modifier.verticalScroll(rememberScrollState())) {
        CalenderView(
            Modifier
                .width(400.dp)
                .height(400.dp),
            Modifier.background(Color.Gray),
            Modifier
                .width(400.dp)
                .height(50.dp),
            selectTime = selectTime
        )
    }
}
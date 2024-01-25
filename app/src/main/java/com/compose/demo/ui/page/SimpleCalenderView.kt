package com.compose.demo.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimpleCalendarView(){
    val selectTime = remember {
        mutableStateListOf<Long>()
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        CalenderView(
            Modifier.fillMaxWidth().height(800.dp),
            Modifier.fillMaxWidth().height(700.dp).background(Color.Gray),
            Modifier
                .fillMaxWidth().height(100.dp),
            selectTime = selectTime
        )
    }
}

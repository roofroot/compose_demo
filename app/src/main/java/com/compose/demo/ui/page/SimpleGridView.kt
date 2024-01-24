package com.compose.demo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compose.demo.layout.GridLayout

@Composable
fun SimpleGridView() {
    GridLayout(modifier = Modifier.size(500.dp), columns = 5, rows = 5, fixed = 0) {
        repeat(25) {
            Box(Modifier.background(getRandomColor())) {
            }
        }
    }
}
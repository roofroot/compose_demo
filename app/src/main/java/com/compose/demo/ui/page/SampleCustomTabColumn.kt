package com.compose.demo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compose.demo.layout.CustomTabColumn
@Composable
fun SampleCustomTabColumn() {
    val list = ArrayList<String>()
    for (i in 0..5) {
        list.add("item ${i}")
    }
    val selectedIndex = remember {
        mutableStateOf(0)
    }
    val list2 = ArrayList<String>()
    for (i in 0..100) {
        list2.add("item ${i}")
    }
    val selectedIndex2 = remember {
        mutableStateOf(0)
    }
    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
    ) {
        CustomTabColumn(
            modifier = Modifier
                .background(Color.Gray, RoundedCornerShape(25.dp))
                .width(100.dp)
                .fillMaxHeight(),
            selectedIndex = selectedIndex,
            frontIndicator = false,
            data = list,
            indicatorContent = {
                Box(
                    Modifier
                        .background(Color.Blue, RoundedCornerShape(25.dp))
                        .width(80.dp)
                        .height(40.dp)
                ) {

                }
            }, indicatorAlignment = Alignment.Center
        ) { item: String, index: Int, selected: Boolean ->
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = if (selected) Color.White else Color.Black
                )
            }

        }
        CustomTabColumn(
            modifier = Modifier
                .background(Color.Gray, RoundedCornerShape(25.dp))
                .width(100.dp)
                .fillMaxHeight(),
            selectedIndex = selectedIndex,
            frontIndicator = false,
            data = list,
            indicatorContent = {
                Box(
                    Modifier
                        .background(Color.Blue, RoundedCornerShape(25.dp))
                        .fillMaxWidth()
                        .height(50.dp)
                ) {

                }
            }, autoFixedContent = true
        ) { item: String, index: Int, selected: Boolean ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = if (selected) Color.White else Color.Black
                )
            }

        }
        CustomTabColumn(
            modifier = Modifier
                .background(Color.Gray, RoundedCornerShape(25.dp))
                .width(100.dp)
                .fillMaxHeight(),
            selectedIndex = selectedIndex,
            data = list,
            indicatorContent = {
                Box(
                    Modifier
                        .background(Color.Blue, RoundedCornerShape(25.dp))
                        .width(5.dp)
                        .height(50.dp)
                ) {

                }
            }, autoFixedContent = true
        ) { item: String, index: Int, selected: Boolean ->
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = if (selected) Color.White else Color.Black
                )
            }

        }

        CustomTabColumn(
            modifier = Modifier
                .width(100.dp)
                .height(500.dp)
                .background(Color.Gray, RoundedCornerShape(25.dp))
                .clip(RoundedCornerShape(25.dp))
            ,
            selectedIndex = selectedIndex2,
            data = list2,
            indicatorContent = {
                Box(
                    Modifier
                        .background(Color.Blue, RoundedCornerShape(25.dp))
                        .width(5.dp)
                        .height(50.dp)
                ) {

                }
            },
            autoScroll = true, autoFixedContent = true, placeCount = 5
        ) { item: String, index: Int, selected: Boolean ->
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = if (selected) Color.White else Color.Black
                )
            }

        }
    }

}
package com.compose.demo.ui.page

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.widget.getScrollBarState
import com.compose.demo.widget.itemScrollBar
import com.compose.demo.widget.scrollbar
import com.google.accompanist.coil.rememberCoilPainter

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun SimpleScrollBar() {

    val listState = rememberLazyListState()
    val data = remember {
        mutableListOf<MyListData>()
    }
    val scrollBarState = getScrollBarState()
    if (data.size == 0) {
        for (i in 1..100) {
            var resIcon = R.mipmap.img_3;
            if (i % 4 == 0) {
                resIcon = R.mipmap.img_0
            } else if (i % 3 == 1) {
                resIcon = R.mipmap.img_1
            } else if (i % 3 == 2) {
                resIcon = R.mipmap.img_4
            } else if (i % 3 == 3) {
                resIcon = R.mipmap.img_5
            }
            data.add(MyListData(getRandomColor(), "000000" + i, resIcon))
        }
    }
    key(data) {
        LazyColumn(
            modifier = Modifier
                .scrollbar(state = listState, scrollBarState = scrollBarState)
                .fillMaxSize(), state = listState
        ) {
            itemsIndexed(data, key = { i: Int, myListData: MyListData ->
                myListData.number
            }) { index: Int, item: MyListData ->
                val painter = rememberCoilPainter(request = item.iconRes)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(100.dp)
                        .background(color = item.color)
                        .itemScrollBar(index = index, scrollBarState),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(150.dp),
                        painter = painter,
                        contentScale = ContentScale.FillBounds,
                        contentDescription = ""
                    )

                    Text(text = item.number)
                }
            }
        }
    }


}
package com.compose.demo.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.layout.RefreshAndLoadMoreList
import com.compose.demo.layout.rememberLoadMoreAndRefreshListState
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.delay

@Composable
fun SimpleRefreshAndLoadList() {
    val data = remember {
        mutableListOf<MyListData>()
    }
    splash(data)
    val state = rememberLoadMoreAndRefreshListState()

    RefreshAndLoadMoreList(
        onLoading = {
            delay(3000)
            loadMore(data)
            state.completeLoading()
        },
        onRefresh = {
            delay(3000)
            splash(data)
            state.completeRefresh()
        },
        listData = data,
        state = state
    ) { index: Int, item: MyListData ->
        val painter = rememberCoilPainter(request = item.iconRes)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(100.dp)
                .background(color = item.color),
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

fun loadMore(listData: MutableList<MyListData>) {
    for (i in listData.size..listData.size + 10) {
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
        listData.add(MyListData(getRandomColor(), "000000" + i, resIcon))
    }
}

fun splash(listData: MutableList<MyListData>) {
    listData.clear()
    for (i in listData.size..listData.size + 10) {
        var resIcon = R.mipmap.img_3;
        if (i % 4 == 0) {
            resIcon = R.mipmap.img_0
        } else if (i % 3 == 1) {
            resIcon = R.mipmap.img_1
        } else if (i % 3 == 2) {
            resIcon = R.mipmap.img_4
        }
        listData.add(MyListData(getRandomColor(), "000000" + i, resIcon))
    }
}

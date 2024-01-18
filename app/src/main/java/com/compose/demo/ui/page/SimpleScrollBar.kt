package com.compose.demo.ui.page

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.widget.CustomOption
import com.compose.demo.widget.customChildBlur
import com.compose.demo.widget.customParentBlur
import com.compose.demo.widget.getBlurState
import com.compose.demo.widget.getPentagramShapePath
import com.compose.demo.widget.getScrollBarState
import com.compose.demo.widget.itemScrollBar
import com.compose.demo.widget.scrollbar
import com.google.accompanist.coil.rememberCoilPainter


@Composable
fun SimpleScrollBar() {

    val data = remember {
        mutableListOf<MyListData>()
    }
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
    list(data = data)

}


@Composable
private fun list(data: List<MyListData>) {
    val state = rememberLazyListState()
    val scrollBarState = getScrollBarState()
    LazyColumn(
        modifier = Modifier
            .scrollbar(state = state, scrollBarState = scrollBarState)
            .fillMaxSize(), state = state
    ) {
        itemsIndexed(data) { index: Int, item: MyListData ->
            val painter = rememberCoilPainter(request = item.iconRes)
            Row(
                Modifier
                    .itemScrollBar(index, scrollBarState)
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

}



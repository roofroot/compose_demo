package com.compose.demo.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.layout.PanelLayout
import com.compose.demo.layout.ScrollView
import com.google.accompanist.coil.rememberCoilPainter

data class MyListData3(
    val color: Color,
    val number: String,
    val iconRes: Int,
    val height: Dp,
    val width: Dp
)

@Composable
fun SimplePanelLayout() {
    val data = remember {
        mutableListOf<MyListData3>()
    }
    data.add(MyListData3(getRandomColor(), "000000" + 1, R.mipmap.img_1, 200.dp, 300.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 2, R.mipmap.img_1, 150.dp, 150.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 3, R.mipmap.img_1, 150.dp, 150.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 4, R.mipmap.img_1, 150.dp, 150.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 5, R.mipmap.img_1, 150.dp, 150.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 6, R.mipmap.img_1, 200.dp, 100.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 7, R.mipmap.img_1, 100.dp, 100.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 8, R.mipmap.img_1, 200.dp, 300.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 9, R.mipmap.img_1, 100.dp, 100.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 10, R.mipmap.img_1, 100.dp, 100.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 11, R.mipmap.img_1, 200.dp, 100.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 12, R.mipmap.img_1, 100.dp, 100.dp))
    data.add(MyListData3(getRandomColor(), "000000" + 13, R.mipmap.img_1, 100.dp, 100.dp))



    ScrollView(
        modifier = Modifier,
        scrollBarWidth = 5.dp,
        scrollBarHeight = 50.dp,
        scrollBarColor = Color.Blue
    ) {
        PanelLayout(
            Modifier.border(1.dp, color = Color.Black),
            width = 600.dp, minSize = 50.dp
        ) {
            data.forEachIndexed { index, item ->
                val painter = rememberCoilPainter(request = item.iconRes)

                Column(
                    Modifier
                        .width(item.width)
                        .height(item.height)
                        .background(color = item.color),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(Modifier.padding(top = 20.dp, bottom = 0.dp)) {
                        Image(
                            modifier = Modifier
                                .size(50.dp),
                            painter = painter,
                            contentScale = ContentScale.FillBounds,
                            contentDescription = ""
                        )
                    }


                    Text(text = item.number)
                }

            }
        }
    }

}
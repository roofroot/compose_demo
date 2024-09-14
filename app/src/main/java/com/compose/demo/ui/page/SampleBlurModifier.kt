package com.compose.demo.ui.page

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.widget.CustomOption
import com.compose.demo.widget.customChildBlur
import com.compose.demo.widget.customParentBlur
import com.compose.demo.widget.getBlurState
import com.compose.demo.widget.getPentagramShapePath
import com.google.accompanist.coil.rememberCoilPainter


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SampleBlurModifier() {

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
    val state = getBlurState()
    Box(
    ) {
        LazyColumn(
            modifier = Modifier
                .background(Color.Blue)
                .fillMaxSize()
                .customParentBlur(30f, state)
        ) {
            itemsIndexed(data) { index: Int, item: MyListData ->
                Spacer(modifier = Modifier.height(20.dp).fillMaxWidth().background(Color.White))
                val painter = rememberCoilPainter(request = item.iconRes)
                Row(
                    Modifier
                        .fillMaxWidth()
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
//        Box(
//            modifier = Modifier
//                .padding(10.dp)
//                .customChildBlur(
//                    "text1",
//                    state,
//                    CustomOption(
//                        rectRadiusX = 0.02f,
//                        rectRadiusY = 0.1f,
//                        colorBlend = Color.Black.toArgb()
//                    )
//                )
//                .align(Alignment.CenterStart)
//                .width(500.dp)
//                .height(100.dp)
//        )
//        Box(
//            modifier = Modifier
//                .padding(10.dp)
//                .customChildBlur(
//                    "text2",
//                    state,
//                    CustomOption(shapeType = 1)
//                )
//                .align(Alignment.TopStart)
//                .size(100.dp)
//        )

        Box(
            modifier = Modifier
                .padding(10.dp)
                .customChildBlur(
                    "text3",
                    state,
                    CustomOption(
                        shapeType = 2,
                        path = getPentagramShapePath(Size(100f, 100f)),
                        pathSize = Size(100f, 100f)
                    )
                )
                .align(Alignment.BottomStart)
                .size(200.dp)
        )

    }

}





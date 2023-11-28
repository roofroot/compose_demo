package com.compose.demo.ui.page

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.layout.DraggableLazyColumn
import com.compose.demo.layout.DraggableLazyGrid
import com.compose.demo.layout.DraggableLazyStaggeredGrid
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.launch

data class MyListData(val color: Color, val number: String, val iconRes: Int)
data class MyListData2(val color: Color, val number: String, val iconRes: Int, val height: Dp)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimpleDraggableLazyList() {


    Column {

        val pageState = rememberPagerState()
        val scope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pageState.currentPage) {
            Tab(selected = pageState.currentPage == 0, onClick = {
                scope.launch {
                    pageState.scrollToPage(0)
                }
            }) {
                Text(text = "列表")
            }
            Tab(selected = pageState.currentPage == 1, onClick = {
                scope.launch {
                    pageState.scrollToPage(1)
                }
            }) {
                Text(text = "网格")
            }
            Tab(selected = pageState.currentPage == 2, onClick = {
                scope.launch {
                    pageState.scrollToPage(2)
                }
            }) {
                Text(text = "错位网格")
            }
        }
        HorizontalPager(state = pageState, pageCount = 3) {
            if (it == 0) {
                val data = remember {
                    mutableListOf<MyListData>()
                }
                data.clear()
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
                DraggableLazyColumn(data = data, hoverItemContent = { item, index ->
                    Row(
                        Modifier
                            .alpha(0.5f)
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(100.dp)
                            .background(color = Color.Cyan),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val painter = rememberCoilPainter(request = item.iconRes)
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
                }) { item, index, modifier ->
                    val painter = rememberCoilPainter(request = item.iconRes)
                    Row(
                        modifier
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
            } else if (it == 1) {
                val data = remember {
                    mutableListOf<MyListData>()
                }
                data.clear()
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
                DraggableLazyGrid(
                    modifier = Modifier.wrapContentSize(),
                    rowCount = 5,
                    data = data,
                    hoverItemContent = { item, index ->
                        Column(
                            Modifier
                                .alpha(0.5f)
                                .padding(10.dp)
                                .height(100.dp)
                                .width(100.dp)
                                .background(color = Color.Cyan),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val painter = rememberCoilPainter(request = item.iconRes)
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
                    }) { item, index, modifier ->
                    val painter = rememberCoilPainter(request = item.iconRes)
                    Column(
                        modifier
                            .padding(10.dp)
                            .height(100.dp)
                            .width(100.dp)
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

            } else if (it == 2) {
                val data = remember {
                    mutableListOf<MyListData2>()
                }
                for (i in 1..100) {
                    var resIcon = R.mipmap.img_3;
                    var height = 100.dp
                    if (i % 4 == 0) {
                        resIcon = R.mipmap.img_0
                        height = 100.dp
                    } else if (i % 3 == 1) {
                        resIcon = R.mipmap.img_1
                        height = 150.dp
                    } else if (i % 3 == 2) {
                        height = 200.dp
                        resIcon = R.mipmap.img_4
                    } else if (i % 3 == 3) {
                        height = 300.dp
                        resIcon = R.mipmap.img_5
                    }
                    data.add(MyListData2(getRandomColor(), "000000" + i, resIcon, height))
                }
                DraggableLazyStaggeredGrid(
                    modifier = Modifier.wrapContentSize(),
                    rowCount = 5,
                    data = data,
                    hoverItemContent = { item, index ->

                        Column(
                            Modifier
                                .alpha(0.5f)
                                .padding(10.dp)
                                .height(item.height)
                                .width(100.dp)
                                .background(color = Color.Cyan),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val painter = rememberCoilPainter(request = item.iconRes)
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
                    }) { item, index, modifier ->
                    val painter = rememberCoilPainter(request = item.iconRes)
                    val animHeight = animateDpAsState(targetValue = item.height)
                    Column(
                        modifier
                            .padding(10.dp)
                            .height(animHeight.value)
                            .width(100.dp)
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
}
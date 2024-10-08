package com.desaysv.hmicomponents.compose

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.ui.page.MyListData
import com.compose.demo.ui.page.getRandomColor
import com.desaysv.hmicomponents.compose_lib.layout.DraggableInsertLazyGrid
import com.desaysv.hmicomponents.compose_lib.layout.OrderableItem
import com.desaysv.hmicomponents.compose_lib.layout.orderable
import com.desaysv.hmicomponents.compose_lib.layout.rememberOrderableListState
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SampleDraggableInsertModifier() {


    Column {

        val pageState = rememberPagerState { 2 }
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

        }
        HorizontalPager(state = pageState) {
            if (it == 0) {
                val data = remember {
                    mutableStateListOf<MyListData>()
                }
                LaunchedEffect(Unit){
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
                }
                val scrollState = rememberLazyListState()
                val orderListState = rememberOrderableListState(state = scrollState, data = data)
                LazyColumn(
                    Modifier.orderable(orderListState) { sourceIndex, targetIndex ->
                        data.add(targetIndex, data.removeAt(sourceIndex))
                    }, state = scrollState
                ) {
                    items(data.size, {
                        if (it == 0) {
                            0
                        } else {
                            data[it].hashCode()
                        }
                    }) { index ->
                        OrderableItem(orderableState = orderListState, index = index) {
                            val painter =
                                rememberCoilPainter(request = data[index].iconRes, fadeIn = true)
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .height(100.dp)
                                    .background(color = data[index].color),
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

                                Text(text = data[index].number)
                            }
                        }
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
                DraggableInsertLazyGrid(
                    columCount = 6,
                    data = data,
                    onExchangeEnd = { sourceIndex: Int, targetIndex: Int ->
                        data.add(targetIndex, data.removeAt(sourceIndex))
                    }
                ) { item, index ->
                    val painter = rememberCoilPainter(request = item.iconRes)
                    Column(
                        Modifier
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
            }
        }
    }
}
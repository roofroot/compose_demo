package com.compose.demo.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compose.demo.layout.MultipleSelectableList
import com.compose.demo.layout.SingleSelectableList
import com.compose.demo.layout.rememberSelectableListState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SampleSelectableList() {
    val list = ArrayList<String>()
    for (i in 1..100) {
        list.add("test item ${i}")
    }
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    Column {
        val pageState = rememberPagerState{2}
        val scope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pageState.currentPage) {
            Tab(selected = pageState.currentPage == 0, onClick = {
                scope.launch {
                    pageState.scrollToPage(0)
                }
            }) {
                Text(text = "单选")
            }
            Tab(selected = pageState.currentPage == 1, onClick = {
                scope.launch {
                    pageState.scrollToPage(1)
                }
            }) {
                Text(text = "多选")
            }
        }
        HorizontalPager(state = pageState) {
            if (it == 0) {
                val selectedItemIndex = remember {
                    mutableStateOf(-1)
                }
                SingleSelectableList(
                    data = list,
                    selectItemIndex = selectedItemIndex,
                ) { modifier: Modifier, selected: Boolean, item: String, index: Int ->
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            20.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        RadioButton(selected = selected, null)
                        Text(text = item, color = if (selected) Color.Red else Color.Blue)
                        Button(onClick = {
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    item + " is" +
                                            (if (selected) " selected" else " unselected")
                                )
                            }
                        }) {
                            Text(text = "state")
                        }
                    }
                }
            } else if (it == 1) {
                val selectableListState = rememberSelectableListState<String>()
                MultipleSelectableList(
                    data = list,
                    selectableListState = selectableListState,
                ) { modifier: Modifier, selected: Boolean, item: String, index: Int ->
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            20.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        Checkbox(checked = selected, null)
                        Text(text = item, color = if (selected) Color.Red else Color.Blue)
                        Button(onClick = {
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    item + " is" +
                                            (if (selected) " selected" else " unselected")
                                )
                            }
                        }) {
                            Text(text = "state")
                        }
                    }
                }
            }
        }
    }

    SnackbarHost(snackBarHostState) {
        Box(Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.Black)
                    .padding(10.dp),
                text = it.visuals.message,
                color = Color.White
            )
        }
    }
}
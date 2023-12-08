package com.compose.demo.layout

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun <H, T> DropDownList(
    list: List<Pair<H, List<T>>>,
    contentColumns: Int = 1,
    contentSpaceH: Dp = 0.dp,
    contentSpaceV: Dp = 10.dp,
    headerSpace: Dp = 10.dp,
    singleExpended: Boolean = true,
    autoScroll: Boolean = true,
    dropDownListState: DropDownListState,
    itemContentModifier: Modifier = Modifier.padding(10.dp),
    headerContent: @Composable (item: H, index: Int, expended: Boolean) -> Unit,
    itemContent: @Composable (item: T, index: Int, headerIndex: Int) -> Unit,
) {

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(headerSpace), state = listState) {
        itemsIndexed(list) { headerIndex: Int, item: Pair<H, List<T>> ->
            if (!dropDownListState.stateMap.containsKey(headerIndex)) {
                val expended = remember {
                    mutableStateOf(false)
                }
                dropDownListState.stateMap.put(headerIndex, expended)
            }

            if (dropDownListState.expendedIndex.value == headerIndex && autoScroll && dropDownListState.stateMap[headerIndex]!!.value) {
                scope.launch {
                    delay(600)
                    listState.animateScrollToItem(
                        headerIndex
                    )
                }
            }
            if (singleExpended) {
                if (headerIndex != dropDownListState.expendedIndex.value) {
                    dropDownListState.stateMap[headerIndex]?.value = false
                }
                headerContent.invoke(
                    item.first,
                    headerIndex,
                    (dropDownListState.stateMap[headerIndex]?.value) ?: false
                )
                AnimatedVisibility(
                    visible = (headerIndex == dropDownListState.expendedIndex.value) && (dropDownListState.stateMap[headerIndex]!!.value),
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    GridLayout(
                        modifier = itemContentModifier,
                        columns = contentColumns,
                        spaceH = contentSpaceH,
                        spaceV = contentSpaceV,
                    ) {
                        item.second.forEachIndexed { i: Int, t: T ->
                            itemContent.invoke(t, i, headerIndex)
                        }
                    }
                }
            } else {
                headerContent.invoke(
                    item.first,
                    headerIndex,
                    dropDownListState.stateMap[headerIndex]!!.value
                )
                AnimatedVisibility(
                    visible = (dropDownListState.stateMap[headerIndex]!!.value),
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    GridLayout(
                        modifier = itemContentModifier,
                        columns = contentColumns,
                        spaceH = contentSpaceH,
                        spaceV = contentSpaceV,
                    ) {
                        item.second.forEachIndexed { i: Int, t: T ->
                            itemContent.invoke(t, i, headerIndex)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberDropDownListState(defaultExpendIndex: Int): DropDownListState {
    val dropDownListState = DropDownListState()
    dropDownListState.expendedIndex = remember {
        mutableStateOf(defaultExpendIndex)
    }
    return dropDownListState
}

class DropDownListState {
    lateinit var expendedIndex: MutableState<Int>
    val stateMap = HashMap<Int, MutableState<Boolean>>()
    fun toggle(index: Int) {
        expendedIndex.value = index
        stateMap[index]!!.value = !stateMap[index]!!.value
    }
}
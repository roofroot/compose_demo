package com.compose.demo.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <H, T> DropDownList(
    list: List<Pair<H, List<T>>>,
    contentColumns: Int = 1,
    contentSpaceH: Dp = 0.dp,
    contentSpaceV: Dp = 10.dp,
    headerSpace: Dp = 10.dp,
    singleExpended: Boolean = false,
    expendedIndex: Int = 0,
    itemContentModifier: Modifier = Modifier.padding(10.dp),
    headerContent: @Composable (item: H, index: Int, expended: MutableState<Boolean>, expendedIndex: MutableState<Int>) -> Unit,
    itemContent: @Composable (item: T, index: Int, headerIndex: Int) -> Unit,
) {
    val expendedIndex = remember {
        mutableStateOf(expendedIndex)
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(headerSpace)) {
        itemsIndexed(list) { headerIndex: Int, item: Pair<H, List<T>> ->
            val expended = remember {
                mutableStateOf(false)
            }
            if (singleExpended) {
                headerContent.invoke(item.first, headerIndex, expended, expendedIndex)
                if (headerIndex != expendedIndex.value) {
                    expended.value = false
                }
                AnimatedVisibility(
                    visible = (headerIndex == expendedIndex.value && expended.value) && expended.value,
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
                headerContent.invoke(item.first, headerIndex, expended, expendedIndex)
                AnimatedVisibility(
                    visible = expended.value, enter = expandVertically(), exit = shrinkVertically()
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
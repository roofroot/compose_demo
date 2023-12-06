package com.compose.demo.layout

import android.sax.Element
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun <T> MultipleSelectableList(
    modifier: Modifier = Modifier,
    data: List<T>,
    selectableListState: SelectableListState<T>,
    content: @Composable (selectModifier: Modifier, selected: Boolean, item: T, index: Int) -> Unit
) {
    selectableListState.selectedMap = remember {
        mutableStateMapOf()
    }
    LazyColumn(modifier = modifier) {
        itemsIndexed(data) { index, item ->
            val modifier = Modifier.clickable {
                selectableListState.toggle(index, item)
            }
            var selected = selectableListState.selectedMap.get(index) != null
            content.invoke(modifier, selected, item, index)
        }
    }
}

@Composable
fun <T> SingleSelectableList(
    modifier: Modifier = Modifier,
    data: List<T>,
    selectItemIndex: MutableState<Int>,
    content: @Composable (selectModifier: Modifier, selected: Boolean, item: T, index: Int) -> Unit
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(data) { index, item ->
            val modifier = Modifier.clickable {
                selectItemIndex.value = index
            }
            var selected = selectItemIndex.value == index
            content.invoke(modifier, selected, item, index)
        }
    }
}

@Composable
fun <T> rememberSelectableListState(): MutableState<SelectableListState<T>> {
    return remember {
        mutableStateOf(SelectableListState())
    }
}

class SelectableListState<T> {
    lateinit var selectedMap: MutableMap<Int, T>
    fun clearSelected() {
        selectedMap.clear()
    }

    fun toggle(index: Int, item: T) {
        if (selectedMap[index] == null) {
            selectedMap[index] = item
        } else {
            selectedMap.remove(index)
        }
    }

    fun removeSelectedElement(index: Int) {
        selectedMap.remove(index)
    }

    fun addSelectedElement(index: Int, element: T) {
        selectedMap[index] = element
    }
}
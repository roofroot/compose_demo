package com.compose.demo.layout

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun <T> RefreshAndLoadMoreList(
    canRefresh: Boolean = true,
    canLoadMore: Boolean = true,
    onLoading: suspend () -> Unit,
    onRefresh: suspend () -> Unit,
    listData: MutableList<T>,
    state: LoadMoreAndRefreshListState,
    footerContent: (@Composable () -> Unit) = {
        val infiniteTransition = rememberInfiniteTransition(label = "loading transition")
        val rotateAnimation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = LinearEasing
                )
            ), label = "loading animation"
        )
        Row(
            Modifier
                .height(70.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier
                    .rotate(rotateAnimation)
                    .size(30.dp)
                    .border(
                        5.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                Color.Gray,
                                Color.LightGray,
                                Color.Gray
                            )
                        ),
                        shape = CircleShape
                    )
            ) {

            }
            Text(modifier = Modifier.padding(10.dp), text = "加载中···")
        }

    },
    headerContent: (@Composable () -> Unit) = {
        val infiniteTransition = rememberInfiniteTransition(label = "loading transition")
        val rotateAnimation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = LinearEasing
                )
            ), label = "loading animation"
        )
        Row(
            Modifier
                .height(70.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier
                    .rotate(rotateAnimation)
                    .size(30.dp)
                    .border(
                        5.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                Color.Gray,
                                Color.LightGray,
                                Color.Gray
                            )
                        ),
                        shape = CircleShape
                    )
            ) {

            }
            Text(modifier = Modifier.padding(10.dp), text = "刷新中···")
        }

    },
    content: @Composable (index: Int, item: T) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var maxItem = remember {
        mutableStateOf(0)
    }

    Column(Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = state.onRefresh.value) {
            headerContent()
        }
        LazyColumn(
            Modifier
                .weight(1f),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            userScrollEnabled = !state.onLoading.value && !state.onRefresh.value,
            content = {
                itemsIndexed(listData) { index: Int, item: T ->
                    content(index, item)
                }
            })
        AnimatedVisibility(visible = state.onLoading.value) {
            scope.launch {
                listState.scrollToItem(maxItem.value)
            }
            footerContent()
        }
    }
    if (listState.isScrollInProgress) {
        if (canLoadMore && !listState.canScrollForward) {
            if (!state.onLoading.value) {
                state.onLoading.value = true
                scope.launch {
                    maxItem.value = listData.size - 1
                    onLoading()
                }
            }
        }
        if (canRefresh && !listState.canScrollBackward) {
            if (!state.onRefresh!!.value) {
                state.onRefresh.value = true
                scope.launch {
                    onRefresh()
                }
            }
        }
    }
}

@Composable
fun rememberLoadMoreAndRefreshListState(): LoadMoreAndRefreshListState {
    val loadMoreAndRefreshListState = LoadMoreAndRefreshListState()
    loadMoreAndRefreshListState.onRefresh = remember {
        mutableStateOf(false)
    }
    loadMoreAndRefreshListState.onLoading = remember {
        mutableStateOf(false)
    }
    return loadMoreAndRefreshListState
}

class LoadMoreAndRefreshListState {
    lateinit var onLoading: MutableState<Boolean>
    lateinit var onRefresh: MutableState<Boolean>
    fun completeLoading() {
        onLoading.value = false
    }

    fun completeRefresh() {
        onRefresh.value = false
    }
}


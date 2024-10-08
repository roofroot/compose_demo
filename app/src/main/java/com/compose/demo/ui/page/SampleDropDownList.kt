package com.compose.demo.ui.page

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.layout.DropDownList
import com.compose.demo.layout.rememberDropDownListState
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun SampleDropDownList() {
    val list = remember {
        mutableStateListOf<Pair<String, SnapshotStateList<String>>>()
    }
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    for (i in 0..10) {
        val sublist = remember {
            mutableStateListOf<String>()
        }
        for (j in 0..5) {
            sublist.add("header:${i} item:${j}")
        }
        list.add(Pair("header${i}", sublist))
    }
    val dropDownListState = rememberDropDownListState(defaultExpendIndex = 0)
    DropDownList(
        list = list,
        dropDownListState = dropDownListState,
        headerContent = { item, index, expended ->
            Row(modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        dropDownListState.toggle(index)
                    })
                }
                .height(50.dp)
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item
                )
                val image =
                    AnimatedImageVector.animatedVectorResource(R.drawable.arrow_up_down)
                Image(
                    painter = rememberAnimatedVectorPainter(image, expended),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
        }) { item: String, index: Int, headerIndex: Int ->
        Text(modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    scope.launch {
                        snackBarHostState.showSnackbar("header:${headerIndex},index:${index}")
                    }
                }, onLongPress = {
                    list[headerIndex].second.remove(item)
                })
            }
            .height(50.dp)
            .fillMaxWidth()
            .background(Color.Gray), text = item)
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
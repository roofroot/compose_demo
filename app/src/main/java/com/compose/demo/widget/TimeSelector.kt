package com.compose.demo.widget

import android.util.Log
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

@Composable
fun WhenTimeSelector(str24: String?, selectedResult: (timeStr24: String?) -> Unit) {
    val timeState = rememberTimeSelectorState()
    LaunchedEffect(key1 = str24) {
        if (str24 == null) {
            timeState.clear()
        } else {
            timeState.setTime(str24)
        }
    }
    Box(Modifier.padding(start = 180.dp).fillMaxWidth()) {
        TimeSelector(timeState, selectedResult = selectedResult)
    }
}


@Composable
fun TimeSelector(
    state: TimeSelectorState,
    selectedResult: (timeStr24: String?) -> Unit
) {
    val hour = state.hour
    val minute = state.minute
    var list = ArrayList<Int>()
    var list2 = ArrayList<Int>()
    for (i in 0..23) {
        list.add(i)
    }
    for (i in 0..59) {
        list2.add(i)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            WheelPicker(modifier = Modifier
                .height(198.dp)
                .width(180.dp),
                data = list,
                selectIndex = hour.value + 1,
                visibleCount = 3,
                listState = state.hourListState,
                onSelect = { _, s: Int ->
                    hour.value = s
                    onSelectedAction(hour, minute, selectedResult)
                })

            Spacer(
                modifier = Modifier
                    .height(3.dp)
                    .width(55.dp)
                    .background(
                        Color.Black,
                        RoundedCornerShape(1.5.dp)
                    )
            )
        }
        Spacer(Modifier.width(30.dp))
        Box(
            Modifier
                .fillMaxHeight()
                .width(90.dp), contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = ":",
                style = TextStyle(
                    fontSize = 38.sp,
                    color = Color.Black
                )
            )
        }
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            WheelPicker(modifier = Modifier
                .height(198.dp)
                .width(180.dp),
                data = list2,
                selectIndex = minute.value + 2,
                visibleCount = 3,
                listState = state.minuteListState,
                onSelect = { _, s: Int ->
                    minute.value = s
                    onSelectedAction(hour, minute, selectedResult)
                })

            Spacer(
                modifier = Modifier
                    .height(3.dp)
                    .width(55.dp)
                    .background(
                        Color.Black,
                        RoundedCornerShape(1.5.dp)
                    )
            )
        }
        Spacer(modifier = Modifier.width(50.dp))
    }
}

private fun onSelectedAction(
    hour: MutableState<Int>, minute: MutableState<Int>,
    selectedResult: (timeStr24: String?) -> Unit
) {
    var result24: String? = null
    if (hour.value != -1 && minute.value != -1) {
        result24 =
            timeFormat("${hour.value}:${minute.value}")
    }
    Log.e("time", "str24:${result24}")
    selectedResult(result24)
}

fun timeFormat(inputTime: String): String? {
    val inputFormat = SimpleDateFormat("HH:mm")
    val outputFormat = SimpleDateFormat("HH:mm")
    var outputTime: String? = null
    try {
        val date = inputFormat.parse(inputTime)
        outputTime = outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return outputTime
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    data: List<Int>,
    selectIndex: Int,
    visibleCount: Int,
    modifier: Modifier = Modifier,
    listState: MutableState<LazyListState?>,
    onSelect: (index: Int, item: Int) -> Unit,
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        BoxWithConstraints(modifier = modifier, propagateMinConstraints = true) {
            val density = LocalDensity.current
            val size = data.size
            val count = size
            val pickerHeight = maxHeight
            val pickerHeightPx = density.run { pickerHeight.toPx() }
            val pickerCenterLinePx = pickerHeightPx / 2
            val itemHeight = pickerHeight / visibleCount
            val itemHeightPx = density.run { itemHeight.toPx() }
            listState.value = rememberLazyListState(
                initialFirstVisibleItemIndex = selectIndex,
                initialFirstVisibleItemScrollOffset = ((itemHeightPx - pickerHeightPx) / 2).roundToInt(),
            )

            LazyColumn(
                modifier = Modifier,
                state = listState.value!!,
                flingBehavior = rememberSnapFlingBehavior(listState.value!!),
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                    )
                }
                items(count) { index ->

                    var state = remember {
                        mutableStateOf(0)
                    }
                    var scale = getScale(
                        data = data,
                        index = index,
                        state = state,
                        pickerCenterLinePx = pickerCenterLinePx,
                        itemHeightPx = itemHeightPx,
                        listState = listState.value!!,
                        onSelect = onSelect
                    )
                    Row {
                        ItemMarkLine(
                            itemHeight = itemHeight,
                            state = state,
                            index = index,
                            size = data.size
                        )
                        ItemView(data = data, itemHeight = itemHeight, index = index, scale = scale)
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                    )
                }
            }
        }
    }
}

@Composable
private fun getScale(
    data: List<Int>,
    index: Int,
    state: MutableState<Int>,
    pickerCenterLinePx: Float,
    itemHeightPx: Float,
    listState: LazyListState,
    onSelect: (index: Int, item: Int) -> Unit
): Float {
    val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
    val item = layoutInfo.visibleItemsInfo.find { it.index - 1 == index }
    var scale = 1f
    val startToScroll = remember {
        mutableStateOf(false)
    }
    if (item != null) {
        val itemCenterY = item.offset + item.size / 2
        scale = if (itemCenterY < pickerCenterLinePx) {
            1 - (pickerCenterLinePx - itemCenterY) / itemHeightPx
        } else {
            1 - (itemCenterY - pickerCenterLinePx) / itemHeightPx
        }
        when {
            item.offset < pickerCenterLinePx && item.offset + item.size > pickerCenterLinePx -> {
                if (listState.isScrollInProgress) {
                    startToScroll.value = true
                } else {
                    if (startToScroll.value) {
                        onSelect(index, data[index])
                        startToScroll.value = false
                    }
                }
                state.value = 0
            }

            (item.offset + item.size) < pickerCenterLinePx -> {
                state.value = -1
            }

            item.offset > pickerCenterLinePx -> {
                state.value = 1
            }
        }
    }
    return scale
}

@Composable
fun ItemView(data: List<Int>, itemHeight: Dp, index: Int, scale: Float) {
    val animFontSize =
        animateIntAsState(targetValue = (28 + 10 * scale).roundToInt())
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (data[index] == -1) "--" else if (data[index] < 10) "0${data[index]}" else data[index].toString(),
            fontSize = animFontSize.value.sp,
            color = Color.Gray
        )
        Text(
            modifier = Modifier.alpha(scale),
            text = if (data[index] == -1) "--" else if (data[index] < 10) "0${data[index]}" else data[index].toString(),
            fontSize = animFontSize.value.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ItemMarkLine(itemHeight: Dp, state: MutableState<Int>, index: Int, size: Int) {
    Box(
        Modifier
            .width(55.dp)
            .height(itemHeight)
    ) {
        Spacer(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .height(2.dp)
                .width(39.dp)
                .background(
                    Color.Blue,
                    shape = RoundedCornerShape(1.dp)
                )
        )
        if (state.value != 1 && index != size - 1) {
            Spacer(
                modifier = Modifier
                    .alpha(0.2f)
                    .align(Alignment.BottomStart)
                    .height(2.dp)
                    .width(25.dp)
                    .background(Color.Blue, RoundedCornerShape(1.dp))
            )
        }

    }
}

@Composable
fun rememberTimeSelectorState(): TimeSelectorState {
    val timeSelectorState = TimeSelectorState()
    timeSelectorState.hour = remember {
        mutableStateOf(-1)
    }
    timeSelectorState.minute = remember {
        mutableStateOf(-1)
    }
    timeSelectorState.hourListState = remember {
        mutableStateOf(null)
    }
    timeSelectorState.minuteListState = remember {
        mutableStateOf(null)
    }
    return timeSelectorState
}

class TimeSelectorState {
    lateinit var hour: MutableState<Int>
    lateinit var minute: MutableState<Int>
    lateinit var hourListState: MutableState<LazyListState?>
    lateinit var minuteListState: MutableState<LazyListState?>
    suspend fun clear() {
        hourListState.value?.scrollToItem(0)
        minuteListState.value?.scrollToItem(0)
        hour.value = 0
        minute.value = 0
    }

    suspend fun setTime(
        str24: String
    ) {
        try {
            val list = str24.split(":")
            hour.value = list[0].toInt()
            minute.value = list[1].toInt()
            hourListState.value?.scrollToItem(hour.value)
            minuteListState.value?.scrollToItem(minute.value)
        } catch (e: Exception) {
        }
    }

    suspend fun setTime(
        hours: Int, mins: Int
    ) {
        try {
            hour.value = hours
            minute.value = mins
            hourListState.value?.scrollToItem(hour.value)
            minuteListState.value?.scrollToItem(minute.value)
        } catch (e: Exception) {
        }
    }

}


@Preview
@Composable
fun TimeSelectorPreview() {
    var str by remember {
        mutableStateOf<String?>("2:30")
    }
    val timeState = rememberTimeSelectorState()
    val scop = rememberCoroutineScope()
    LaunchedEffect(key1 = str) {
        if (str == null) {
            timeState.clear()
        } else {
            timeState.setTime(str!!)
        }
    }
    Column {
        Text(
            str ?: "空时间"
        )
        TimeSelector(timeState, selectedResult = { time24 ->
            str = time24
        })
        Text(
            modifier = Modifier.clickable {
                scop.launch {
                    str = null
                }
            },
            text = "清空"
        )
    }
}

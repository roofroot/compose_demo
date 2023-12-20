package com.compose.demo.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.R
import com.compose.demo.layout.GridLayout
import com.compose.demo.widget.CalenderUtil
import java.util.Date

@Composable
fun CalenderView(
    viewModifier: Modifier,
    daySelectViewModifier: Modifier,
    monthSelectViewModifier: Modifier,
    selectTime: MutableList<Long>
) {
    val monthTime = remember {
        mutableStateOf(CalenderUtil.getCurrentMonth(System.currentTimeMillis()))
    }
    Column(
        modifier = viewModifier,
        verticalArrangement = Arrangement.Top
    ) {
        MonthSelectBar(monthSelectViewModifier, monthTime = monthTime)
        DaySelectView(
            daySelectViewModifier,
            selectTime = selectTime,
            monthTime = monthTime,
            onDayClick = { time ->
                if (selectTime.contains(time)) {
                    selectTime.remove(time)
                } else {
                    selectTime.add(time)
                }
            },
            onDisableDayClick = { time ->
                onDisableDayClick(time, monthTime, selectTime)
            })
    }
}

private fun onDisableDayClick(
    time: Long,
    monthTime: MutableState<Long>,
    selectTime: MutableList<Long>
) {
    if (time < monthTime.value) {
        monthTime.value = CalenderUtil.getLastMonth(monthTime.value)
        if (selectTime.contains(time)) {
            selectTime.remove(time)
        } else {
            selectTime.add(time)
        }
    } else if (time > monthTime.value) {
        monthTime.value = CalenderUtil.getNextMonth(monthTime.value)
        if (selectTime.contains(time)) {
            selectTime.remove(time)
        } else {
            selectTime.add(time)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DaySelectView(
    modifier: Modifier,
    selectTime: MutableList<Long>,
    monthTime: MutableState<Long>,
    onDayClick: (time: Long) -> Unit,
    onDisableDayClick: (time: Long) -> Unit
) {
    AnimatedContent(
        targetState = monthTime.value,
        transitionSpec = {
            ContentTransformAnim(initialState, targetState)
        },
        contentAlignment = Alignment.TopCenter
    ) { time ->
        GridLayout(
            modifier = modifier,
            columns = 7,
            rows = 7,
            fixed = true,
            spaceV = 1.dp,
            spaceH = 1.dp
        ) {
            weekBar()
            dayView(
                selectTime = selectTime,
                monthTime = time,
                onDayClick = onDayClick,
                onDisableDayClick = onDisableDayClick
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MonthSelectBar(monthViewModifier: Modifier, monthTime: MutableState<Long>) {
    Box(
        monthViewModifier
    ) {
        val leftEnable = remember {
            mutableStateOf(true)
        }
        val rightEnable = remember {
            mutableStateOf(true)
        }
        Image(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterStart)
                .clickable {
                    monthTime.value = CalenderUtil.getLastMonth(monthTime.value)
                },
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = "",
            colorFilter = if (leftEnable.value) null else ColorFilter.tint(Color.Gray)
        )


        AnimatedContent(
            modifier = Modifier
                .align(Alignment.Center)
                .width(480.dp),
            targetState = monthTime.value,
            transitionSpec = {
                ContentTransformAnim(initialState, targetState)
            },
            contentAlignment = Alignment.Center
        ) { time ->
            val date = Date(time)
            GradientText(
                modifier = Modifier.fillMaxSize(),
                text = "${stringResource(id = CalenderUtil.getMonthString(date = date))} ${
                    CalenderUtil.getYear(
                        Date(
                            time
                        )
                    )
                }"
            )
        }

        Image(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(10.dp)
                .clickable {
                    monthTime.value = CalenderUtil.getNextMonth(monthTime.value)
                },
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "",
            colorFilter = if (rightEnable.value) null else ColorFilter.tint(Color.Gray)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun ContentTransformAnim(initialState: Long, targetState: Long): ContentTransform {
    return if (targetState!! < initialState!!) {
        slideInHorizontally { fullWidth -> fullWidth } + fadeIn() with slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
    } else if (targetState!! > initialState!!) {
        slideInHorizontally { fullWidth -> -fullWidth } + fadeIn() with slideOutHorizontally { fullWidth -> fullWidth } + fadeOut()
    } else {
        EnterTransition.None with ExitTransition.None
    }
}

@Composable
fun dayView(
    selectTime: MutableList<Long>,
    monthTime: Long,
    onDayClick: (time: Long) -> Unit,
    onDisableDayClick: (time: Long) -> Unit
) {
    val monthData = CalenderUtil.getMonthData(monthTime)
    var count = 0
    for (i in monthData.firstDayOfStartDaysWeek until monthData.firstDayOfStartDaysWeek + monthData.startDayOfWeek - 1) {
        grayDateView(
            CalenderUtil.getLastMonthDay(monthTime, i),
            str = i.toString(),
            onDisableDayClick
        )
        count++
    }
    for (i in monthData.startDay..monthData.endDay) {
        whiteDateView(
            selectTime,
            CalenderUtil.getCurrentMonthDay(monthTime, i),
            str = i.toString(),
            onDayClick
        )
        count++
    }
    for (i in 1..7 - monthData.endDayOfWeek) {
        grayDateView(
            CalenderUtil.getNextMonthDay(monthTime, i),
            str = i.toString(),
            onDisableDayClick
        )
    }
}

@Composable
fun grayDateView(
    dayTime: Long,
    str: String,
    onDayClick: (time: Long) -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color.LightGray)
            .clickable {
                onDayClick(dayTime)
            }, contentAlignment = Alignment.Center
    ) {
        Text(text = str)
    }
}

@Composable
fun whiteDateView(
    selectTime: MutableList<Long>,
    dayTime: Long,
    str: String,
    onDayClick: (time: Long) -> Unit
) {
    Box(
        modifier = Modifier
            .background(if (selectTime.contains(dayTime)) Color.Black else Color.White)
            .clickable {
                onDayClick(dayTime)
            }, contentAlignment = Alignment.Center
    ) {
        Text(text = str, color = if (selectTime.contains(dayTime)) Color.White else Color.Black)
    }
}

@Composable
fun weekBar() {
    val weekDataArr = listOf(
        stringResource(id = R.string.calendar_SUN),
        stringResource(id = R.string.calendar_MON),
        stringResource(id = R.string.calendar_TUE),
        stringResource(id = R.string.calendar_WED),
        stringResource(id = R.string.calendar_TUE),
        stringResource(id = R.string.calendar_FRI),
        stringResource(id = R.string.calendar_SAT)
    )
    weekDataArr.forEach {
        Box(contentAlignment = Alignment.Center) {
            Text(text = it)
        }
    }
}

@Preview
@Composable
fun CalenderViewPreview() {
    val selectTime = remember {
        mutableStateListOf<Long>()
    }
    Column(Modifier.verticalScroll(rememberScrollState())) {
        CalenderView(
            Modifier
                .width(200.dp)
                .height(400.dp),
            Modifier.background(Color.Gray),
            Modifier
                .width(200.dp)
                .height(50.dp),
            selectTime = selectTime
        )
    }
}
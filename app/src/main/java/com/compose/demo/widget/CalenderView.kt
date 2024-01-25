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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.R
import com.compose.demo.layout.GridLayout
import java.util.Date

/**
 *
 * @param viewModifier Modifier 日历控件整体的modifier
 * @param daySelectViewModifier Modifier 底部日期区域的modifier
 * @param monthSelectViewModifier Modifier 月份选择控件的modifier
 * @param selectTime MutableList<Long> 当前选中的日期列表
 * @param activeDay [@androidx.compose.runtime.Composable] Function4<[@kotlin.ParameterName] MutableList<Long>, [@kotlin.ParameterName] Long, [@kotlin.ParameterName] String, [@kotlin.ParameterName] Function1<[@kotlin.ParameterName] Long, Unit>, Unit>
 *    可以通过修改这个参数自定义当前选择月份的日期的显示样式，
 * @param inactiveDay [@androidx.compose.runtime.Composable] Function3<[@kotlin.ParameterName] Long, [@kotlin.ParameterName] String, [@kotlin.ParameterName] Function1<[@kotlin.ParameterName] Long, Unit>, Unit>
 *     可以通过修改这个参数自定义当前页的不属于本月的日期的样式
 * @param topWeekBar [@androidx.compose.runtime.Composable] Function0<Unit>
 *     可以通过修改这个参数自定义顶部标注星期的样式
 * @param monthSelectBar [@androidx.compose.runtime.Composable] Function2<[@kotlin.ParameterName] Modifier, [@kotlin.ParameterName] MutableState<Long>, Unit>
 *     可以通过修改这个参数自定义顶部月份选择控件的样式
 * @param fixed Int 日期部分的自适应效果，0,item的宽度高度都按照总高度与数量平均，1，item的宽度按照总宽度与总数平均，
 *  * 2，item的宽度按照总高度与数量平均，默认值是0
 */
@Composable
fun CalenderView(
    viewModifier: Modifier,
    daySelectViewModifier: Modifier,
    monthSelectViewModifier: Modifier,
    selectTime: MutableList<Long>,
    activeDay: @Composable (
        selectTime: MutableList<Long>, dayTime: Long, dayStr: String, onDayClick: (time: Long) -> Unit
    ) -> Unit = { selectTime: MutableList<Long>, dayTime: Long, str: String, onDayClick: (time: Long) -> Unit ->
        whiteDateView(
            selectTime = selectTime, dayTime = dayTime, str = str, onDayClick = onDayClick
        )
    },
    inactiveDay: @Composable (dayTime: Long, dayStr: String, onDayClick: (time: Long) -> Unit) -> Unit = { dayTime: Long, str: String, onDayClick: (time: Long) -> Unit ->
        grayDateView(dayTime = dayTime, str = str, onDayClick = onDayClick)
    },
    topWeekBar: @Composable () -> Unit = {
        weekBar()
    },
    monthSelectBar: @Composable (monthViewModifier: Modifier, monthTime: MutableState<Long>) -> Unit = { modifier: Modifier, monthTime: MutableState<Long> ->
        MonthSelectBar(monthViewModifier = modifier, monthTime = monthTime)
    },
    fixed: Int = 0,
) {
    val monthTime = remember {
        mutableStateOf(CalenderUtil.getCurrentMonth(System.currentTimeMillis()))
    }
    Column(
        modifier = viewModifier, verticalArrangement = Arrangement.Top
    ) {
        monthSelectBar(monthSelectViewModifier, monthTime = monthTime)
        AnimatedContent(
            targetState = monthTime.value, transitionSpec = {
                ContentTransformAnim(initialState, targetState)
            }, contentAlignment = Alignment.Center, label = ""
        ) { monthTimeLong ->
            GridLayout(
                modifier = daySelectViewModifier,
                columns = 7,
                rows = 7,
                fixed = fixed,
                spaceV = 1.dp,
                spaceH = 1.dp
            ) {
                topWeekBar()
                val monthData = CalenderUtil.getMonthData(monthTimeLong)
                var count = 0
                for (i in monthData.firstDayOfStartDaysWeek until monthData.firstDayOfStartDaysWeek + monthData.startDayOfWeek - 1) {
                    inactiveDay(
                        CalenderUtil.getLastMonthDay(monthTime.value, i), dayStr = i.toString()
                    ) {
                        onDisableDayClick(it, monthTime, selectTime)
                    }
                    count++
                }
                for (i in monthData.startDay..monthData.endDay) {
                    activeDay(
                        selectTime = selectTime,
                        dayTime = CalenderUtil.getCurrentMonthDay(monthTime.value, i),
                        dayStr = i.toString(), onDayClick = { time ->
                            if (selectTime.contains(time)) {
                                selectTime.remove(time)
                            } else {
                                selectTime.add(time)
                            }
                        }
                    )
                    count++
                }
                for (i in 1..7 - monthData.endDayOfWeek) {
                    inactiveDay(
                        CalenderUtil.getNextMonthDay(monthTime.value, i), dayStr = i.toString()
                    ) {
                        onDisableDayClick(it, monthTime, selectTime)
                    }
                }
            }
        }
    }
}

private fun onDisableDayClick(
    time: Long, monthTime: MutableState<Long>, selectTime: MutableList<Long>
) {
    if (time < monthTime.value) {
        monthTime.value = CalenderUtil.getLastMonth(monthTime.value)
    } else if (time > monthTime.value) {
        monthTime.value = CalenderUtil.getNextMonth(monthTime.value)
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
            Text(
                modifier = Modifier.wrapContentSize(),
                text = "${stringResource(id = CalenderUtil.getMonthString(date = date))} ${
                    CalenderUtil.getYear(
                        Date(
                            time
                        )
                    )
                }",
                fontSize = 33.sp
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
fun grayDateView(
    dayTime: Long, str: String, onDayClick: (time: Long) -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color.LightGray)
            .clickable {
                onDayClick(dayTime)
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = str, style = TextStyle(
                color = Color.Black, fontSize = 25.sp
            )
        )
    }
}

@Composable
fun whiteDateView(
    selectTime: MutableList<Long>, dayTime: Long, str: String, onDayClick: (time: Long) -> Unit
) {
    Box(
        modifier = Modifier
            .background(if (selectTime.contains(dayTime)) Color.Black else Color.White)
            .clickable {
                onDayClick(dayTime)
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = str, style = TextStyle(
                color = if (selectTime.contains(dayTime)) Color.White else Color.Black,
                fontSize = 25.sp
            )
        )
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
                .width(620.dp)
                .height(692.dp),
            Modifier.background(Color.Gray),
            Modifier
                .width(620.dp)
                .height(100.dp),
            selectTime = selectTime
        )
    }

}
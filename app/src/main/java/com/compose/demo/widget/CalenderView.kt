package com.compose.demo.widget

import android.annotation.SuppressLint
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
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
import kotlinx.coroutines.launch
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
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
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
    monthSelectBar: @Composable (monthViewModifier: Modifier, monthTime: MutableState<Long>, pagerState: PagerState, index: Int) -> Unit = { modifier: Modifier, monthTime: MutableState<Long>, pagerState, index ->
        MonthSelectBar(
            monthViewModifier = modifier,
            monthTime = monthTime,
            pagerState = pagerState,
            index = index
        )
    },
    fixed: Int = 0,
) {
    val pagerState = rememberPagerState {
        Int.MAX_VALUE
    }
    val scope = rememberCoroutineScope()

    //对滑动到int最大值和0时做一下处理，虽然实际只用中，应该很少有机会会执行这段代码
    if (pagerState.currentPage == Int.MAX_VALUE) {
        scope.launch {
            pagerState.scrollToPage(1000 * 3 + pagerState.currentPage)
        }
    } else if (pagerState.currentPage == 0) {
        scope.launch {
            pagerState.scrollToPage(1000 * 3)
        }
    }
    val monthTime = remember {
        mutableStateOf(CalenderUtil.getCurrentMonth(System.currentTimeMillis()))
    }
    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collect { newValue ->
            if (olderPagerIndex != -1) {
                if (pagerState.currentPage > olderPagerIndex) {
                    olderPagerIndex = pagerState.currentPage
                    monthTime.value = CalenderUtil.getNextMonth(monthTime.value)
                } else if (pagerState.currentPage < olderPagerIndex) {
                    olderPagerIndex = pagerState.currentPage
                    monthTime.value = CalenderUtil.getLastMonth(monthTime.value)
                }
            } else {
                olderPagerIndex = pagerState.currentPage
            }
        }
    }
    Box(
        modifier = viewModifier
    ) {
        HorizontalPager(state = pagerState) { index ->
            Column(Modifier.fillMaxSize()) {
                monthSelectBar(
                    monthSelectViewModifier,
                    monthTime = monthTime,
                    pagerState,
                    index
                )

                GridLayout(
                    modifier = daySelectViewModifier,
                    columns = 7,
                    rows = 7,
                    fixed = fixed,
                    spaceV = 1.dp,
                    spaceH = 1.dp
                ) {
                    topWeekBar()
                    val time =
                        if (index > pagerState.currentPage) CalenderUtil.getNextMonth(monthTime.value)
                        else if (index < pagerState.currentPage) CalenderUtil.getLastMonth(
                            monthTime.value
                        ) else monthTime.value
                    val monthData = CalenderUtil.getMonthData(time)
                    var count = 0
                    for (i in monthData.firstDayOfStartDaysWeek until monthData.firstDayOfStartDaysWeek + monthData.startDayOfWeek - 1) {
                        inactiveDay(
                            CalenderUtil.getLastMonthDay(time, i), dayStr = i.toString()
                        ) {
                            if (index == pagerState.currentPage) {
                                if (it < monthTime.value) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                } else if (it > monthTime.value) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            }
                        }
                        count++
                    }
                    for (i in monthData.startDay..monthData.endDay) {
                        activeDay(selectTime = selectTime,
                            dayTime = CalenderUtil.getCurrentMonthDay(time, i),
                            dayStr = i.toString(),
                            onDayClick = { time ->
                                if (index == pagerState.currentPage) {
                                    if (selectTime.contains(time)) {
                                        selectTime.remove(time)
                                    } else {
                                        selectTime.add(time)
                                    }
                                }
                            })
                        count++
                    }
                    for (i in 1..7 - monthData.endDayOfWeek) {
                        inactiveDay(
                            CalenderUtil.getNextMonthDay(time, i), dayStr = i.toString()
                        ) {
                            if (index == pagerState.currentPage) {
                                if (it < monthTime.value) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                } else if (it > monthTime.value) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        Box(monthSelectViewModifier) {
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
//                    monthTime.value = CalenderUtil.getLastMonth(monthTime.value)
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "",
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(10.dp)
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "",
            )
        }
    }
}


var olderPagerIndex = -1

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun MonthSelectBar(
    monthViewModifier: Modifier,
    monthTime: MutableState<Long>,
    pagerState: PagerState,
    index: Int,
) {
    Box(
        monthViewModifier
    ) {
        val time = if (index > pagerState.currentPage) CalenderUtil.getNextMonth(monthTime.value)
        else if (index < pagerState.currentPage) CalenderUtil.getLastMonth(
            monthTime.value
        ) else monthTime.value
        var date = Date(time)
        Text(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center),
            text = "${stringResource(id = CalenderUtil.getMonthString(date = date))} ${
                CalenderUtil.getYear(
                    date
                )
            }",
            fontSize = 33.sp
        )
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

@OptIn(ExperimentalFoundationApi::class)
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
package com.compose.demo.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


/**
 * 带滚动条的ScrollView，可以设置滚动条样式
 * 主要解决Compose中api没有提供滚动条控件的问题
 */
@Composable
fun ScrollView(
    modifier: Modifier, scrollBarWidth: Dp=5.dp, scrollBarHeight: Dp=50.dp, scrollBarColor: Color=Color.Black, content: @Composable () -> Unit
) {
    val scrollState=  rememberScrollState()
    val scrollViewHeight = remember{
        mutableStateOf(0)
    }
    val scrollViewWidth = remember{
        mutableStateOf(0)
    }
    Box(Modifier.wrapContentSize().onPlaced {
        scrollViewHeight.value=it.size.height
        scrollViewWidth.value=it.size.width
    }) {
        Column(
            modifier.verticalScroll(scrollState)
        ) {
            content()
        }
        AnimatedVisibility(visible = scrollState.isScrollInProgress, enter = EnterTransition.None) {
            val a=scrollViewHeight.value-scrollBarHeight.value* LocalDensity.current.density
            //这一行的好意思是 视口的高度减去进度条的高度
            var x = a/ (scrollState.maxValue)
            //用这个值除以控件总高度减去视口的高度， 就可以得到我们窗口实际滚动的距离与进度条实际可以滚动的范围的一个比例
            val offsetY=if(scrollState.value>0) ((scrollState.value * x)/ LocalDensity.current.density).roundToInt().dp else 0.dp
            //将这个比例值乘以实际滚动的距离，就能得出进度条应该显示的位置了
            Box(
                modifier = Modifier.offset(
                    (scrollViewWidth.value/ LocalDensity.current.density).dp-scrollBarWidth,
                    offsetY
                ).width(scrollBarWidth).height(scrollBarHeight).background(scrollBarColor)
            ) {

            }
        }
    }
}
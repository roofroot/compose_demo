package com.compose.demo.widget

import android.util.Log
import com.compose.demo.R
import java.util.Calendar
import java.util.Date


data class MonthData(
    val startDay: Int,
    val endDay: Int,
    val firstDayOfStartDaysWeek: Int,
    val startDayOfWeek: Int,
    val endDayOfWeek: Int,
)

class CalenderUtil {
    companion object {
        private val monthNames = arrayOf(
            R.string.calendar_January,
            R.string.calendar_February,
            R.string.calendar_March,
            R.string.calendar_April,
            R.string.calendar_May,
            R.string.calendar_June,
            R.string.calendar_July,
            R.string.calendar_August,
            R.string.calendar_September,
            R.string.calendar_October,
            R.string.calendar_November,
            R.string.calendar_December
        )

        /**
         * 获得当前月的第一天
         *
         * HH:mm:ss SS为零
         *
         * @return
         */
        fun getMonthData(long: Long = System.currentTimeMillis()): MonthData {
            var cal = Calendar.getInstance()
            cal.time = Date(long)
            Log.e("aaaaaa", cal.toString())
            cal[Calendar.DAY_OF_MONTH] = 1 // M月置1
            cal[Calendar.HOUR_OF_DAY] = 0 // H置零
            cal[Calendar.MINUTE] = 0 // m置零
            cal[Calendar.SECOND] = 0 // s置零
            cal[Calendar.MILLISECOND] = 0 // S置零
            var startDayOfWeek = cal[Calendar.DAY_OF_WEEK]
            var startDay = cal[Calendar.DAY_OF_MONTH]

            cal[Calendar.DAY_OF_WEEK] = 1
            var firstDayOfStartDaysWeek = cal[Calendar.DAY_OF_MONTH]
            cal = Calendar.getInstance()
            cal.time = Date(long)
            cal[Calendar.DAY_OF_MONTH] = 1 // M月置零
            cal[Calendar.HOUR_OF_DAY] = 0 // H置零
            cal[Calendar.MINUTE] = 0 // m置零
            cal[Calendar.SECOND] = 0 // s置零
            cal[Calendar.MILLISECOND] = 0 // S置零
            cal[Calendar.MONTH] = cal[Calendar.MONTH] + 1 // 月份+1
            cal[Calendar.MILLISECOND] = -1 // 毫秒-1
            var endDay = cal[Calendar.DAY_OF_MONTH]
            var endDayOfWeek = cal[Calendar.DAY_OF_WEEK]
            return MonthData(
                startDay,
                endDay,
                firstDayOfStartDaysWeek,
                startDayOfWeek,
                endDayOfWeek
            )
        }

        fun getNextMonth(long: Long = System.currentTimeMillis()): Long {
            var cal = Calendar.getInstance()
            cal.time = Date(long)
            cal[Calendar.DAY_OF_MONTH] = 1 // M月置零
            cal[Calendar.HOUR_OF_DAY] = 0 // H置零
            cal[Calendar.MINUTE] = 0 // m置零
            cal[Calendar.SECOND] = 0 // s置零
            cal[Calendar.MILLISECOND] = 0 // S置零
            cal[Calendar.MONTH] = cal[Calendar.MONTH] + 1 // 月份+1
            return cal.timeInMillis
        }

        fun getLastMonth(long: Long = System.currentTimeMillis()): Long {
            var cal = Calendar.getInstance()
            cal.time = Date(long)
            cal[Calendar.DAY_OF_MONTH] = 1 // M月置零
            cal[Calendar.HOUR_OF_DAY] = 0 // H置零
            cal[Calendar.MINUTE] = 0 // m置零
            cal[Calendar.SECOND] = 0 // s置零
            cal[Calendar.MILLISECOND] = 0 // S置零
            cal[Calendar.MONTH] = cal[Calendar.MONTH] - 1 // 月份+1
            return cal.timeInMillis
        }

        fun getCurrentMonth(long: Long = System.currentTimeMillis()): Long {
            var cal = Calendar.getInstance()
            cal.time = Date(long)
            cal[Calendar.DAY_OF_MONTH] = 1 // M月置零
            cal[Calendar.HOUR_OF_DAY] = 0 // H置零
            cal[Calendar.MINUTE] = 0 // m置零
            cal[Calendar.SECOND] = 0 // s置零
            cal[Calendar.MILLISECOND] = 0 // S置零
            return cal.timeInMillis
        }

        fun getCurrentDayOfMonth(): Long {
            var cal = Calendar.getInstance()
            cal.time = Date(System.currentTimeMillis())
            cal[Calendar.HOUR_OF_DAY] = 0 // H置零
            cal[Calendar.MINUTE] = 0 // m置零
            cal[Calendar.SECOND] = 0 // s置零
            cal[Calendar.MILLISECOND] = 0 // S置零
            return cal.timeInMillis
        }

        fun getLastMonthDay(monthTime: Long = System.currentTimeMillis(), day: Int): Long {
            val cal = Calendar.getInstance()
            cal.time = Date(monthTime)
            cal[Calendar.MONTH] = cal[Calendar.MONTH] - 1
            cal[Calendar.DAY_OF_MONTH] = day
            return cal.timeInMillis
        }

        fun getNextMonthDay(monthTime: Long = System.currentTimeMillis(), day: Int): Long {
            val cal = Calendar.getInstance()
            cal.time = Date(monthTime)
            cal[Calendar.MONTH] = cal[Calendar.MONTH] + 1
            cal[Calendar.DAY_OF_MONTH] = day
            return cal.timeInMillis
        }

        fun getCurrentMonthDay(monthTime: Long = System.currentTimeMillis(), day: Int): Long {
            val cal = Calendar.getInstance()
            cal.time = Date(monthTime)
            cal[Calendar.DAY_OF_MONTH] = day
            return cal.timeInMillis
        }


        /**
         * 获得年份
         * @param date
         * @return
         */
        fun getYear(date: Date?): Int {
            val c = Calendar.getInstance()
            c.time = date
            return c[Calendar.YEAR]
        }

        /**
         * 获得月份
         * @param date
         * @return
         */
        fun getMonth(date: Date?): Int {
            val c = Calendar.getInstance()
            c.time = date
            return c[Calendar.MONTH] + 1
        }

        /**
         * 获得月份
         * @param date
         * @return
         */

        fun getMonthString(date: Date?): Int {
            val c = Calendar.getInstance()
            c.time = date
            return monthNames[c[Calendar.MONTH]]
        }

        /**
         * 获得星期几
         * @param date
         * @return
         */
        fun getWeek(date: Date?): Int {
            val c = Calendar.getInstance()
            c.time = date
            return c[Calendar.WEEK_OF_YEAR]
        }

        /**
         * 获得日期
         * @param date
         * @return
         */
        fun getDay(date: Date?): Int {
            val c = Calendar.getInstance()
            c.time = date
            return c[Calendar.DATE]
        }
    }
}
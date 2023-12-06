package com.compose.demo.util

import android.util.Log

/**
 * liShan
 */
object LogUtil {
    private const val TAG = "LogUtil"
    var DEBUG = true
    fun V(info: String) {
        if (DEBUG) {
            val infos = autoJumpLogInfos
            Log.v(TAG, info + " : " + infos[1] + infos[2])
        }
    }

    fun D(info: String) {
        if (DEBUG) {
            val infos = autoJumpLogInfos
            Log.v(TAG, info + " : " + infos[1] + infos[2])
        }
    }

    fun I(info: String) {
        if (DEBUG) {
            val infos = autoJumpLogInfos
            Log.v(TAG, info + " : " + infos[1] + infos[2])
        }
    }

    fun W(info: String) {
        if (DEBUG) {
            val infos = autoJumpLogInfos
            Log.v(TAG, info + " : " + infos[1] + infos[2])
        }
    }

    fun E(info: String) {
        if (DEBUG) {
            val infos = autoJumpLogInfos
            Log.v(TAG, info + " : " + infos[1] + infos[2])
        }
    }

    /**
     * 显示Log信息（带行号）
     *
     * @param logLevel 1 v ; 2 d ; 3 i ; 4 w ; 5 e .
     * @param info     显示的log信息
     */
    fun showLogWithLineNum(logLevel: Int, info: String) {
        val infos = autoJumpLogInfos
        when (logLevel) {
            1 -> if (DEBUG) Log.v(infos[0], info + " : " + infos[1] + infos[2])
            2 -> if (DEBUG) Log.d(infos[0], info + " : " + infos[1] + infos[2])
            3 -> if (DEBUG) Log.i(infos[0], info + " : " + infos[1] + infos[2])
            4 -> if (DEBUG) Log.w(infos[0], info + " : " + infos[1] + infos[2])
            5 -> if (DEBUG) Log.e(infos[0], info + " : " + infos[1] + infos[2])
        }
    }

    private val autoJumpLogInfos: Array<String>
        /**
         * 获取打印信息所在方法名，行号等信息
         *
         * @return
         */
        private get() {
            val infos = arrayOf("", "", "")
            val elements = Thread.currentThread().stackTrace
            return if (elements.size < 5) {
                Log.e("LogUtil", "Stack is too shallow!!!")
                infos
            } else {
                infos[0] = elements[4].className.substring(
                    elements[4].className.lastIndexOf(".") + 1
                )
                infos[1] = elements[4].methodName + "()"
                infos[2] = (" at (" + elements[4].className + ".java:"
                        + elements[4].lineNumber + ")")
                infos
            }
        }
}
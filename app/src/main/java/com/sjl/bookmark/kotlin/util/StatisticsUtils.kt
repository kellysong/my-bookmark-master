package com.sjl.bookmark.kotlin.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename StatisticsUtils
 * @time 2022/12/3 10:47
 * @copyright(C) 2022 song
 */
object StatisticsUtils {

    /**
     * 获取一周的日期X轴
     * @return Array<String>
     */
    fun getWeekDateX(): Array<String> {
        val list2: MutableList<String> = mutableListOf<String>()
        val sdf = SimpleDateFormat("MM-dd");
        for (i in 6 downTo 0) {
            val calendar = Calendar.getInstance()
            calendar[Calendar.DAY_OF_YEAR] = calendar[Calendar.DAY_OF_YEAR] - i
            val today = calendar.time
            list2.add(sdf.format(today))

        }
        val toTypedArray = list2.toTypedArray()
        return toTypedArray
    }

    /**
     * 阅读统计Y轴
     * @return IntArray
     */
    fun getCountY(): IntArray {
        val list2: MutableList<Int> = mutableListOf<Int>()
        for (i in 0 until 60 step 10) {
            list2.add(i)
        }
        val toIntArray = list2.toIntArray()
        return toIntArray
    }

}
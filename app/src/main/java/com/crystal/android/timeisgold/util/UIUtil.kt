package com.crystal.android.timeisgold.util

import android.util.Log
import java.text.DecimalFormat

class UIUtil {
    companion object {
        fun getDurationTime(time: Long) : String {
            val hour: Long
            val min: Long
            val sec: Long

            if (time >= 3600) {
                hour = time / 3600
                val extra = time % 3600
                min = extra / 60
                sec = extra % 60
            } else {
                hour = 0
                min = time / 60
                sec = time % 60
            }

            return "${"%02d".format(hour)}:${"%02d".format(min)}:${"%02d".format(sec)}"
        }
    }
}
package com.crystal.android.timeisgold.util

import android.content.Context
import android.text.format.DateFormat.getBestDateTimePattern
import android.text.format.DateFormat.getTimeFormat
import android.util.Log
import com.crystal.android.timeisgold.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.*

class DateUtil {
    companion object {
        fun dateToString(date: Date): String? {
            val formatter = SimpleDateFormat("yyyy/MM/dd/ HH:mm aa", Locale.getDefault())
            return formatter.format(date)
        }

        fun dateToTimeString(date: Date): String {
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            return formatter.format(date).toString()
        }

        fun longToDurationTime(time: Long): String {
            val hour: Long
            val min: Long

            if (time >= 3600) {
                hour = time / 3600
                val extra = time % 3600
                min = extra / 60
            } else {
                hour = 0
                min = time / 60
            }

            return "${"%02d".format(hour)}:${"%02d".format(min)}"
        }

        fun differDates(date1: Date, date2: Date): Boolean {
            val cal1 = Calendar.getInstance().apply {
                this.time = date1
                this.set(Calendar.HOUR, 0)
                this.set(Calendar.MINUTE, 0)
                this.set(Calendar.SECOND, 0)
                this.set(Calendar.MILLISECOND, 0)
            }
            val cal2 = Calendar.getInstance().apply {
                this.time = date2
                this.set(Calendar.HOUR, 0)
                this.set(Calendar.MINUTE, 0)
                this.set(Calendar.SECOND, 0)
                this.set(Calendar.MILLISECOND, 0)
            }
            val differ = cal1.time.time - cal2.time.time
            return differ == 0L
        }

        fun getDayOfWeekString(context: Context, dayOfWeek: Int): String {

           return when (dayOfWeek) {
                1 -> {
                    context.getString(R.string.calendar_sunday)
                }
                2 -> {
                    context.getString(R.string.calendar_monday)
                }
                3 -> {
                    context.getString(R.string.calendar_tuesday)
                }
                4 -> {
                    context.getString(R.string.calendar_wednesday)
                }
                5 -> {
                    context.getString(R.string.calendar_thursday)
                }
                6 -> {
                    context.getString(R.string.calendar_friday)
                }
                7 -> {
                    context.getString(R.string.calendar_saturday)
                }
               else -> {
                   "none"
               }
            }
        }


    }
}

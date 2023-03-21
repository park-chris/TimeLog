package com.crystal.android.timeisgold.util

import android.content.Context
import android.text.format.DateFormat.getBestDateTimePattern
import android.text.format.DateFormat.getTimeFormat
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

        fun differDates(date1: Date, date2: Date): Boolean {
            val differ = date1.time - date2.time
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

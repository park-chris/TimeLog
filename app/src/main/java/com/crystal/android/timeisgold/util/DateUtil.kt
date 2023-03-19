package com.crystal.android.timeisgold.util

import android.content.Context
import android.text.format.DateFormat.getBestDateTimePattern
import android.text.format.DateFormat.getTimeFormat
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

    }
}

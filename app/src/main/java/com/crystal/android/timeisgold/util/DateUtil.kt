package com.crystal.android.timeisgold.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DateUtil {
    companion object {
        fun dateFormat(date: Date): Date? {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val formatString = formatter.format(date)

            return formatter.parse(formatString)
        }
    }
}
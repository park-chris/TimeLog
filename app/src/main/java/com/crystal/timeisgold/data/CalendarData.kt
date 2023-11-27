package com.crystal.timeisgold.data

import java.util.*

data class CalendarData(
     var date: Date = Date(),
     var isSelected: Boolean = false,
     var hasRecord: Boolean = false
) {
}
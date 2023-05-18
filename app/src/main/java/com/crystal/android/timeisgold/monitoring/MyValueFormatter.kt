package com.crystal.android.timeisgold.monitoring

import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class MyValueFormatter: ValueFormatter() {
    private val format = DecimalFormat ("###,##")

    override fun getPointLabel(entry: Entry?): String {
        return format.format(entry?.y) + "h"
    }

    override fun getFormattedValue(value: Float): String {

        val hour = value / 3600
        val extra = value % 3600
        val min = extra / 60

        return "${format.format(hour.toInt())}h ${format.format(min.toInt())}m"
    }
}
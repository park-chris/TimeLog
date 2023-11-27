package com.crystal.timeisgold.history

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class CenterSmoothScroller : LinearSmoothScroller {
    constructor(context: Context) : super(context)

    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int = (boxStart + (boxEnd - boxStart) / 5) - (viewStart + (viewEnd - viewStart) / 5)
}
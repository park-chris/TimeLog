package com.crystal.android.timeisgold.history

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

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
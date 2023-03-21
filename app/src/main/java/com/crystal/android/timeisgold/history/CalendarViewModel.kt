package com.crystal.android.timeisgold.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class CalendarViewModel: ViewModel() {

    private val _currentDate = MutableLiveData<Date>()
    val currentDate: LiveData<Date> = _currentDate

    private val _currentDay = MutableLiveData<Date>()
    val currentDay: LiveData<Date> = _currentDay

    fun updateCurrentDate(date: Date) {
        _currentDate.value = date
    }

    fun updateCurrentDay(date: Date) {
        _currentDay.value = date
    }

    companion object {
        private var mInstance: CalendarViewModel? = null

        @Synchronized
        fun getCalendarViewModelInstance(): CalendarViewModel {
            if (mInstance == null) {
                mInstance = CalendarViewModel()
            }
            return mInstance as CalendarViewModel
        }
    }

}
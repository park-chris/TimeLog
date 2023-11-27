package com.crystal.timeisgold.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class CalendarViewModel: ViewModel() {

    private val _currentCalendarDate = MutableLiveData<Date>()
    val currentCalendarDate: LiveData<Date> = _currentCalendarDate

    private val _currentSelectDay = MutableLiveData<Date>()
    val currentSelectDay: LiveData<Date> = _currentSelectDay

    fun updateCurrentCalendar(date: Date) {
        _currentCalendarDate.value = date
    }

    fun updateCurrentSelect(date: Date) {
        _currentSelectDay.value = date
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
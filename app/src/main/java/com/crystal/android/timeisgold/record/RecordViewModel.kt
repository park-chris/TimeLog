package com.crystal.android.timeisgold.record

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crystal.android.timeisgold.data.Record
import androidx.lifecycle.Transformations
import java.text.SimpleDateFormat
import java.util.*

class RecordViewModel: ViewModel() {

    private val recordRepository = RecordRepository.get()
    private val recordIdLiveData = MutableLiveData<UUID>()
    private val dateLiveData = MutableLiveData<Date>()

    val recordListLiveData = recordRepository.getRecords()

    var recordLiveDate: LiveData<Record?> = Transformations.switchMap(recordIdLiveData) { recordId ->
        recordRepository.getRecord(recordId)
    }

    val recordDailyLiveData = recordRepository.getDailyTime()

    var dailyTimeList: LiveData<Record?> = Transformations.switchMap(recordIdLiveData) {recordId ->
        recordRepository.getRecord(recordId)
    }

    var selectedRecordsLiveData: LiveData<List<Record>> = Transformations.switchMap(dateLiveData) {date ->

        val cal1 = Calendar.getInstance()
        cal1.time = date

        cal1.set(Calendar.HOUR, 0)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        cal1.set(Calendar.MILLISECOND, 0)

        val date1 = cal1.time.time

        cal1.set(Calendar.HOUR, 23)
        cal1.set(Calendar.MINUTE, 59)
        cal1.set(Calendar.SECOND, 59)

        val date2 =cal1.time.time

         recordRepository.getSelectedRecords(date1, date2)
    }


    fun loadRecord(recordId: UUID) {
        recordIdLiveData.value = recordId
    }

    fun loadRecords(date: Date) {
        dateLiveData.value = date
    }

    fun saveRecord(record: Record) {
        recordRepository.updateRecord(record)
    }

    fun addRecord(record: Record) {
        recordRepository.addRecord(record)
    }

    fun deleteRecord(record: Record) {
        recordRepository.deleteRecord(record)
    }
}
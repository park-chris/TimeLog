package com.crystal.android.timeisgold.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crystal.android.timeisgold.data.Record
import androidx.lifecycle.Transformations
import java.util.*

class RecordViewModel: ViewModel() {

    private val recordRepository = RecordRepository.get()
    private val recordIdLiveData = MutableLiveData<UUID>()

    val recordListLiveData = recordRepository.getRecords()

    var recordLiveDate: LiveData<Record?> = Transformations.switchMap(recordIdLiveData) { recordId ->
        recordRepository.getRecord(recordId)
    }

    val recordDailyLiveData = recordRepository.getDailyTime()

    var dailyTimeList: LiveData<Record?> = Transformations.switchMap(recordIdLiveData) {recordId ->
        recordRepository.getRecord(recordId)
    }

    fun loadRecord(recordId: UUID) {
        recordIdLiveData.value = recordId
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
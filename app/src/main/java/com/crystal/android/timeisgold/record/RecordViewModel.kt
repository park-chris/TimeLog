package com.crystal.android.timeisgold.record

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crystal.android.timeisgold.data.Record
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    fun updateRecord(record: Record) {
        recordRepository.updateRecord(record)
    }

    fun addRecord(record: Record) {
        recordRepository.addRecord(record)
    }

    fun deleteRecord(record: Record) {
        recordRepository.deleteRecord(record)
    }

    fun getCheckRecordsSum(date: Date): List<Date> {
        val cal1 = Calendar.getInstance()
        cal1.time = date

        cal1.set(Calendar.HOUR, 0)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        cal1.set(Calendar.MILLISECOND, 0)
        cal1.set(Calendar.DAY_OF_MONTH, 1)

        val date1 = cal1.time.time

        cal1.set(Calendar.HOUR, 23)
        cal1.set(Calendar.MINUTE, 59)
        cal1.set(Calendar.SECOND, 59)
        cal1.set(Calendar.DAY_OF_MONTH, cal1.getActualMaximum(Calendar.DAY_OF_MONTH))

        val date2 =cal1.time.time

        return recordRepository.getCheckRecordsSum(date1, date2)
    }


    fun getDailyRecords(): List<Record> {
        val cal1 = Calendar.getInstance()
        val date1 = cal1.time.time
        cal1.set(Calendar.HOUR, 0)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        cal1.add(Calendar.DATE, -7)
        val date2 =cal1.time.time
        return recordRepository.getDailyTime(date2, date1)
    }

    suspend fun getTime(item: String): Int {

        val cal1 = Calendar.getInstance()
        cal1.set(Calendar.HOUR, 0)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)

        val date1 = cal1.time.time
        cal1.set(Calendar.HOUR, 23)
        cal1.set(Calendar.MINUTE, 59)
        cal1.set(Calendar.SECOND, 59)

        val date2 =cal1.time.time

        return withContext(Dispatchers.IO) {
            recordRepository.getTime(date1, date2, item)
        }
    }

}
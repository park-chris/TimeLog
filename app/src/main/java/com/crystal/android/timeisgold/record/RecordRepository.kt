package com.crystal.android.timeisgold.record

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.crystal.android.timeisgold.data.Record
import java.util.*
import java.util.concurrent.Executors


private const val DATABASE_NAME = "record-database"

class RecordRepository private constructor(context: Context){

    private val database: RecordDatabase = Room.databaseBuilder(
        context.applicationContext,
        RecordDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val recordDao = database.RecordDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getRecords(): LiveData<List<Record>> = recordDao.getRecords()
    fun getRecord(id: UUID): LiveData<Record?> = recordDao.getRecord(id)
    fun getTime(item: String): Int = recordDao.getTime(item)
    fun getDailyTime(): LiveData<List<Record>> = recordDao.getDailyTime()
    fun getSelectedRecords(startMilli: Long, endMilli: Long): LiveData<List<Record>> = recordDao.getSelectedRecords(startMilli, endMilli)

    fun updateRecord(record: Record) {
        executor.execute {
            recordDao.updateRecord(record)
        }
    }

    fun addRecord(record: Record) {
        executor.execute{
            recordDao.addRecord(record)
        }
    }

    fun deleteRecord(record: Record) {
        executor.execute {
            recordDao.deleteRecord(record)
        }
    }

    companion object {

        private var INSTANCE: RecordRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = RecordRepository(context)
            }
        }

        fun get():RecordRepository {
            return INSTANCE ?: throw IllegalStateException("RecordRepository must be initialized")
        }

    }

}
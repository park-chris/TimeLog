package com.crystal.timeisgold.record

import androidx.lifecycle.LiveData
import androidx.room.*
import com.crystal.timeisgold.data.Record
import java.util.*


@Dao
interface RecordDao {

    @Query("SELECT * FROM record ORDER BY startDate DESC")
    fun getRecords(): LiveData<List<Record>>

    @Query("SELECT * FROM record WHERE id=(:id)")
    fun getRecord(id: UUID): LiveData<Record?>

    @Query("SELECT sum(durationTime) FROM record WHERE startDate  BETWEEN :startMilliSec AND :endMilliSec  AND type=(:type)")
    fun getTime(startMilliSec: Long, endMilliSec: Long, type: String): Int

    @Query("SELECT * FROM record WHERE startDate BETWEEN :startMilliSec AND :endMilliSec ORDER BY startDate DESC")
    fun getDailyTime(startMilliSec: Long, endMilliSec: Long): List<Record>

    @Query("SELECT * FROM record WHERE startDate BETWEEN :startMilliSec AND :endMilliSec ORDER BY startDate ASC")
    fun getSelectedRecords(startMilliSec: Long, endMilliSec: Long): LiveData<List<Record>>

    @Query("SELECT startDate FROM record WHERE startDate BETWEEN :startMilliSec AND :endMilliSec ORDER BY startDate ASC")
    fun getCheckRecordsSum(startMilliSec: Long, endMilliSec: Long): List<Date>

    @Update
    fun updateRecord(record: Record)

    @Insert
    fun addRecord(record: Record)

    @Delete
    fun deleteRecord(record: Record)
}
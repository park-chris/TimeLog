package com.crystal.timeisgold.record

import androidx.room.*
import com.crystal.timeisgold.data.Record

@Database(entities = [Record::class], version = 1)
@TypeConverters(RecordTypeConverters::class)
abstract class RecordDatabase: RoomDatabase() {

    abstract fun RecordDao(): RecordDao

}
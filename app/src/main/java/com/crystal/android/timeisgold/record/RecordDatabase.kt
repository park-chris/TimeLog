package com.crystal.android.timeisgold.record

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.crystal.android.timeisgold.data.Record

@Database(entities = [Record::class], version = 1)
@TypeConverters(RecordTypeConverters::class)
abstract class RecordDatabase: RoomDatabase() {

    abstract fun RecordDao(): RecordDao

}
package com.crystal.android.timeisgold.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Record(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),
    var type: String = "",
    var date: Date = Date(),
    var created: String = "",
    var durationTime: Long = 0,
    var memo: String = ""
) {

}

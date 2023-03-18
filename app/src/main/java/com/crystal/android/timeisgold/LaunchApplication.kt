package com.crystal.android.timeisgold

import android.app.Application
import com.crystal.android.timeisgold.record.RecordRepository

class LaunchApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        RecordRepository.initialize(this)
    }
}
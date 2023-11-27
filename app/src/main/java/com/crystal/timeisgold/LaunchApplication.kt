package com.crystal.timeisgold

import android.app.Application
import com.crystal.timeisgold.record.RecordRepository

class LaunchApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        RecordRepository.initialize(this)
    }
}
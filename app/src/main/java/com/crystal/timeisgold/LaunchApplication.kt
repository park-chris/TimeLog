package com.crystal.timeisgold

import android.app.Application
import com.crystal.timeisgold.record.RecordRepository
import com.google.android.gms.ads.MobileAds

class LaunchApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        RecordRepository.initialize(this)
        MobileAds.initialize(this)
    }
}
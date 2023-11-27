package com.crystal.timeisgold.util

import android.app.ActivityManager
import android.content.Context

class ServiceUtil {
    companion object {
        @SuppressWarnings("deprecation")
        fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val am: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in  am.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name.equals(service.service.className)) {
                    return true
                }
            }
            return false
        }

    }
}
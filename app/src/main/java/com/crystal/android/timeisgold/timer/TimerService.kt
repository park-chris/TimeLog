package com.crystal.android.timeisgold.timer

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.crystal.android.timeisgold.MainActivity
import com.crystal.android.timeisgold.R
import com.crystal.android.timeisgold.util.UIUtil
import java.util.*
import kotlin.concurrent.timer

private const val CHANNEL_ID_TIMER = "timer_notification_channel"
private const val CHANNEL_NAME_TIMER = "tig_timer"
private const val NOTIFICATION_ID_TIMER = 1919


private const val TAG = "TimerServiceLog"

class TimerService : Service() {

    companion object {
        const val ACTION_CLOSE = "action_close"
        const val ACTION_SAVE = "action_save"
        const val ACTION_UPDATE = "action_update"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_START = "action_start"
        const val ACTION_MOVE_TO_FOREGROUND = "action_move_to_foreground"
        const val ACTION_MOVE_TO_BACKGROUND = "action_move_to_background"

        const val TIMER_VALUE = "timer_value"
    }

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var timer: Timer? = null
    private var second: Long = 0
    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_CLOSE -> {
                    Log.d(TAG, "action close!!")
                    timer?.cancel()
                    notificationManager?.cancel(NOTIFICATION_ID_TIMER)
                    stopSelf()
                }
                ACTION_SAVE -> {
                    Log.d(TAG, "action save!!")
                }
                ACTION_UPDATE -> {
                    //
                }
                ACTION_PAUSE -> {
                    timer?.cancel()
                    timer = null
                }
                ACTION_START -> {
                    startTimer()
                }
                ACTION_MOVE_TO_BACKGROUND -> {
                    moveToBackground()
                }
                ACTION_MOVE_TO_FOREGROUND -> {
                    moveToForeground()
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        initNotification()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startTimer()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        notificationManager?.cancel(NOTIFICATION_ID_TIMER)
        val intent = Intent(ACTION_CLOSE)
        intent.putExtra(TIMER_VALUE, second)
        sendBroadcast(intent)
        applicationContext.unregisterReceiver(receiver)
        stopSelf()
    }

    private fun initNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent: PendingIntent
        val closePendingIntent: PendingIntent
        val closeIntent = Intent().apply {
            action = ACTION_CLOSE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0 or PendingIntent.FLAG_MUTABLE)
            closePendingIntent = PendingIntent.getBroadcast(this, 1, closeIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            closePendingIntent = PendingIntent.getBroadcast(this, 1, closeIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID_TIMER)
            .setSmallIcon(R.drawable.ic_app_logo_foreground)
            .setContentTitle(getString(R.string.timer_notification_title))
            .setContentText(getString(R.string.timer_notification_content, 0,0,0) )
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_close, getString(R.string.finish), closePendingIntent)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_TIMER,
                CHANNEL_NAME_TIMER,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager!!.createNotificationChannel(channel)
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_CLOSE)
        intentFilter.addAction(ACTION_SAVE)
        intentFilter.addAction(ACTION_UPDATE)
        intentFilter.addAction(ACTION_PAUSE)
        intentFilter.addAction(ACTION_START)
        intentFilter.addAction(ACTION_MOVE_TO_BACKGROUND)
        intentFilter.addAction(ACTION_MOVE_TO_FOREGROUND)
        applicationContext.registerReceiver(receiver, intentFilter)

        val notification = notificationBuilder!!.build()

        notificationManager!!.notify(NOTIFICATION_ID_TIMER, notification)

    }

    private fun updateNotificationUI(time: Long) {
        notificationBuilder!!.setContentText(UIUtil.getDurationTime(time))

        notificationManager!!.notify(NOTIFICATION_ID_TIMER, notificationBuilder!!.build())

        val intent = Intent(ACTION_UPDATE)
        intent.putExtra(TIMER_VALUE, second)
        sendBroadcast(intent)
    }

    private fun startTimer() {
        timer = Timer()
        timer = timer(initialDelay = 0, period = 1000) {
            second++
            updateNotificationUI(second)
        }
    }

    private fun moveToBackground() {
        Log.d(TAG, "moveToBackground")

        stopForeground(true)
    }

    private fun moveToForeground() {
        Log.d(TAG, "moveToForeGround")

        val notification = notificationBuilder!!.build()
        startForeground(NOTIFICATION_ID_TIMER, notification)

    }


}
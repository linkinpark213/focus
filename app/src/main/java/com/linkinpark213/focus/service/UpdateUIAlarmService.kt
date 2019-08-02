package com.linkinpark213.focus.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import com.linkinpark213.focus.AlarmReceiver

class UpdateUIAlarmService : Service() {
    override fun onCreate() {
        println("Events-fetching Service is ON")
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("Events-fetching Service's onBind() called!")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val updateUIIntent = Intent()
        updateUIIntent.action = "com.linkinpark213.update"
        updateUIIntent.putExtra("data", "213")
        sendBroadcast(updateUIIntent)

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timePeriod: Int = 10 * 1000
        val triggerAtTime: Long = SystemClock.elapsedRealtime() + timePeriod
        val i = Intent(this, AlarmReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, i, 0)
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent)
        return super.onStartCommand(intent, flags, startId)
    }

}
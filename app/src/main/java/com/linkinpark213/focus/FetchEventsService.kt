package com.linkinpark213.focus

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import com.google.gson.Gson
import com.linkinpark213.focus.tasks.AsyncGetCalendarListTask

class FetchEventsService : Service() {
    override fun onCreate() {
        println("Events-fetching Service is ON")
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("Events-fetching Service's onBind() called!")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val calendarManager = Gson().fromJson(intent!!.getStringExtra("calendarManager"), CalendarManager::class.java)
        AsyncGetCalendarListTask(calendarManager).execute()

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timePeriod: Int = 1 * 1000
        val triggerAtTime: Long = SystemClock.elapsedRealtime() + timePeriod
        val i = Intent(this, AlarmReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, i, 0)
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent)

        return super.onStartCommand(intent, flags, startId)
    }

}
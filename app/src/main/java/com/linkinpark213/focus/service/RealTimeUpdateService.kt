package com.linkinpark213.focus.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Message
import android.os.SystemClock
import com.linkinpark213.focus.AlarmReceiver
import com.linkinpark213.focus.CalendarManager
import com.linkinpark213.focus.task.AsyncGetCalendarListTask

class RealTimeUpdateService : Service() {
    private var calendarManager: CalendarManager? = null

    override fun onCreate() {
        println("Events-fetching Service is ON")
        this.calendarManager = CalendarManager(
            this.applicationContext,
            "daiki2kobayashi@gmail.com"
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("Events-fetching Service's onBind() called!")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Check current calendar situation
        AsyncGetCalendarListTask(this.calendarManager!!).execute()

        // Send request to update MainActivity UI
        updateMainUI()

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timePeriod: Int = 10 * 1000
        val triggerAtTime: Long = SystemClock.elapsedRealtime() + timePeriod
        val i = Intent(this, AlarmReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, i, 0)
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateMainUI() {
        val ongoingEventSummary = when {
            this.calendarManager!!.ongoingEvent != null -> this.calendarManager!!.ongoingEvent!!.summary
            else -> "None"
        }
        val ongoingEventStartTime = when {
            this.calendarManager!!.ongoingEvent != null -> this.calendarManager!!.ongoingEvent!!.start.dateTime.value
            else -> 0
        }
        val ongoingEventEndTime = when {
            this.calendarManager!!.ongoingEvent != null -> this.calendarManager!!.ongoingEvent!!.end.dateTime.value
            else -> 0
        }
        val incomingEventSummary = when {
            this.calendarManager!!.incomingEvent != null -> this.calendarManager!!.incomingEvent!!.summary
            else -> "None"
        }
        val incomingEventStartTime = when {
            this.calendarManager!!.incomingEvent != null -> this.calendarManager!!.incomingEvent!!.start.dateTime.value
            else -> 0
        }
        val incomingEventEndTime = when {
            this.calendarManager!!.incomingEvent != null -> this.calendarManager!!.incomingEvent!!.end.dateTime.value
            else -> 0
        }

        println("Currently ongoing event: $ongoingEventSummary")
        println("Currently incoming event: $incomingEventSummary")

        val updateUIIntent = Intent()
        updateUIIntent.action = "com.linkinpark213.focus.updateui"
        updateUIIntent.putExtra("ongoingEventSummary", ongoingEventSummary)
        updateUIIntent.putExtra("ongoingEventStartTime", ongoingEventStartTime)
        updateUIIntent.putExtra("ongoingEventEndTime", ongoingEventEndTime)
        updateUIIntent.putExtra("incomingEventSummary", incomingEventSummary)
        updateUIIntent.putExtra("incomingEventStartTime", incomingEventStartTime)
        updateUIIntent.putExtra("incomingEventEndTime", incomingEventEndTime)
        sendBroadcast(updateUIIntent)
    }

}
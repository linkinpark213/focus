package com.linkinpark213.focus.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import com.linkinpark213.focus.util.AlarmReceiver
import com.linkinpark213.focus.calendar.CalendarManager
import com.linkinpark213.focus.task.AsyncGetCalendarListTask

class RealTimeUpdateService : Service() {
    private var calendarManager: CalendarManager? = null
    private var accountName: String? = null

    companion object {
        const val interval = 5
    }

    override fun onCreate() {
        println("Events-fetching Service is ON")
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("Events-fetching Service's onBind() called!")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (this.calendarManager == null) {
            this.accountName = intent!!.getStringExtra("accountName")
            this.calendarManager = CalendarManager(
                this.applicationContext,
                this.accountName!!
            )
        }
        // Check current calendar situation
        AsyncGetCalendarListTask(this.calendarManager!!).execute()

        // Send request to update MainActivity UI
        if (calendarManager!!.initialized)
            updateMainUI()

        // Send request to update Floating Window
        updateFloatingWindow()

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timePeriod: Int = interval * 1000
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

        println("Ongoing: $ongoingEventSummary, Incoming: $incomingEventSummary")

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

    private fun updateFloatingWindow() {
        val windowOnIntent = Intent()
        windowOnIntent.setAction("com.linkinpark213.focus.triggerwindow")
        if (calendarManager!!.ongoingEvent != null) {
            windowOnIntent.putExtra("on", true)
        } else {
            windowOnIntent.putExtra("on", false)
        }
        sendBroadcast(windowOnIntent)
    }

}
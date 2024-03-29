package com.linkinpark213.focus.task

import android.os.AsyncTask
import com.google.api.client.util.DateTime
import com.linkinpark213.focus.calendar.CalendarManager
import java.lang.Exception

class AsyncGetCalendarListTask(var calendarManager: CalendarManager) : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
        try {
            if (calendarManager.focusCalendar == null) {
                val calendars = calendarManager.client!!.CalendarList().list().execute().items
                for (calendar in calendars) {
                    if (calendar.summary == "Focus") {
                        calendarManager.focusCalendar = calendar
                        println("Calendar named \"Focus\" found. The ID is: ${calendarManager.focusCalendar!!.id}")
                    }
                }
            }
            if (calendarManager.focusCalendar == null) {
                println("Calendar named \"Focus\" is not found. Creating...")
                calendarManager.client!!.calendars()
                    .insert(
                        com.google.api.services.calendar.model.Calendar().setSummary("Focus")
                    ).execute()
                println("Created.")
            }

            val currentTime = DateTime(System.currentTimeMillis())
            print("Current time: $currentTime, ")
            val calendar = calendarManager.client!!.calendars().get(calendarManager.focusCalendar!!.id).execute()
            val events = calendarManager.client!!.events()
                .list(calendar.id)
                .setSingleEvents(true)
                .setOrderBy("starttime")
                .setTimeMin(currentTime)
                .execute().items
            print(events.size)
            println(" event(s) found.")
            if (events.size > 0) {
                val event = events[0]
                if (event.start.dateTime.value > currentTime.value) {
                    calendarManager.ongoingEvent = null
                    calendarManager.incomingEvent = event
                } else {
                    calendarManager.ongoingEvent = event
                    if (events.size > 1) {
                        calendarManager.incomingEvent = events[1]
                    } else {
                        calendarManager.incomingEvent = null
                    }
                }
            } else {
                calendarManager.ongoingEvent = null
                calendarManager.incomingEvent = null
            }
            calendarManager.initialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}
package com.linkinpark213.focus.tasks

import android.os.AsyncTask
import android.widget.Toast
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.util.DateTime
import com.linkinpark213.focus.CalendarManager

class AsyncGetCalendarListTask(var calendarManager: CalendarManager) : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
        try {
            val calendars = calendarManager.client!!.CalendarList().list().execute().items
            for (calendar in calendars) {
                if (calendar.summary == "Focus") {
                    calendarManager.focusCalendar = calendar
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
            println("Current time: $currentTime")
            println("Calendar named \"Focus\" found. The ID is: ${calendarManager.focusCalendar!!.id}")
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
            }
            calendarManager.printCurrentEvents()

        } catch (e: UserRecoverableAuthIOException) {
            println("Caught!")
        }
        return ""
    }
}
package com.linkinpark213.focus.tasks

import android.os.AsyncTask
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.CalendarListEntry
import com.linkinpark213.focus.MainActivity

class AsyncGetCalendarListTask(var activity: MainActivity, var service: Calendar) : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
        try {
            val calendarList = this.service.CalendarList().list().execute()
            val items = calendarList.items
            for (i in 0 until items.size) {
                if (items[i].summary == "Focus") {
                    activity.focusCalendar = items[i]
                }
            }
            if (activity.focusCalendar == null) {
                println("Calendar named \"Focus\" is not found. Creating...")
                this.service.CalendarList().insert(CalendarListEntry().setId("Focus"))
            } else {
                println("Calendar named \"Focus\" found. The ID is:")
                println(activity.focusCalendar!!.id)
                val calendar = this.service.calendars().get(activity.focusCalendar!!.id).execute()
                println(calendar.summary)
                calendar.forEach { o -> println(o.key + o.value) }

            }
        } catch (e: UserRecoverableAuthIOException) {
            println("Caught!")
        }
        return ""
    }
}
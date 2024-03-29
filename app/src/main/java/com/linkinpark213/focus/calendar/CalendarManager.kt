package com.linkinpark213.focus.calendar

import android.accounts.Account
import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.CalendarListEntry
import com.google.api.services.calendar.model.Event
import java.util.*

class CalendarManager(
    applicationContext: Context, accountName: String
) {
    private var credential: GoogleAccountCredential? = null
    var client: Calendar? = null
    var focusCalendar: CalendarListEntry? = null
    var ongoingEvent: Event? = null
    var incomingEvent: Event? = null
    var initialized: Boolean = false

    init {
        this.credential = GoogleAccountCredential.usingOAuth2(
            applicationContext,
            Collections.singleton(CalendarScopes.CALENDAR)
        )
        this.credential!!.selectedAccount = Account(accountName, "com.linkinpark213.focus")

        this.client = Calendar.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Focus").build()
    }


}
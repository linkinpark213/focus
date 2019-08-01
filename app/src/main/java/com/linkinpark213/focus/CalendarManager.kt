package com.linkinpark213.focus

import android.accounts.Account
import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.CalendarListEntry
import com.google.api.services.calendar.model.Event
import java.util.*

class CalendarManager(
    applicationContext: Context, accountName: String, ongoingEventTextView: TextView,
    incomingEventTextView: TextView
) {
    private var credential: GoogleAccountCredential? = null
    var client: Calendar? = null
    var focusCalendar: CalendarListEntry? = null
    var ongoingEvent: Event? = null
    var comingEvent: Event? = null

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

    fun printCurrentEvents() {
        println(
            "Currently ongoing event: ${when {
                ongoingEvent != null -> ongoingEvent!!.summary
                else -> "None"
            }}"
        )
        println(
            "Currently incoming event: ${when {
                comingEvent != null -> comingEvent!!.summary
                else -> "None"
            }}"
        )
    }

}
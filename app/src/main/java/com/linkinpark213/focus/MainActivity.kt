package com.linkinpark213.focus

import android.accounts.Account
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.CalendarListEntry
import com.google.api.services.calendar.model.Event
import com.linkinpark213.focus.tasks.AsyncGetCalendarListTask
import java.util.*

class MainActivity : AppCompatActivity() {
    private var calendarManager: CalendarManager? = null

    companion object {
        const val REQUEST_ACCOUNTS = 1
        const val REQUEST_AUTHORIZATION = 2
        const val REQUEST_READ_CALENDAR = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.calendarManager = CalendarManager(this.applicationContext, "daiki2kobayashi@gmail.com")

        val mainButton = findViewById<Button>(R.id.button)

        AsyncGetCalendarListTask(this.calendarManager!!).execute()

        // EventListeners
        mainButton.setOnClickListener {
            run {
                AsyncGetCalendarListTask(this.calendarManager!!).execute()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        print("Request code: ")
        println(
            when (requestCode) {
                REQUEST_ACCOUNTS -> "REQUEST_ACCOUNTS"
                REQUEST_AUTHORIZATION -> "REQUEST_AUTHORIZATION"
                REQUEST_READ_CALENDAR -> "REQUEST_READ_CALENDAR"
                else -> "UNKNOWN REQUEST"
            }
        )
        print("Result code: ")
        println(
            when (resultCode) {
                Activity.RESULT_OK -> "OK"
                Activity.RESULT_CANCELED -> "CANCELED"
                Activity.RESULT_FIRST_USER -> "FIRST_USER"
                else -> "UNKNOWN RESULT"
            }
        )
        print("Data: ")
        println(data.toString())
        when (requestCode) {
            REQUEST_ACCOUNTS -> {
                println("Requested accounts")
            }
            REQUEST_AUTHORIZATION -> {
                println("Requested authorization")
            }
            REQUEST_READ_CALENDAR -> {
                println("Requested reading calendar")
                AsyncGetCalendarListTask(this.calendarManager!!).execute()
                val notifyIntent = Intent(this, this.javaClass)
                notifyIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                
            }
        }
    }
}

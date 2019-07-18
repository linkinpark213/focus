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
import com.linkinpark213.focus.tasks.AsyncGetCalendarListTask
import java.util.*

class MainActivity : AppCompatActivity() {
    private var client: Calendar? = null
    private var credential: GoogleAccountCredential? = null
    var focusCalendar: CalendarListEntry? = null

    companion object {
        const val REQUEST_ACCOUNTS = 1
        const val REQUEST_AUTHORIZATION = 2
        const val REQUEST_READ_CALENDAR = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.credential = GoogleAccountCredential.usingOAuth2(
            applicationContext,
            Collections.singleton(CalendarScopes.CALENDAR)
        )
        this.credential!!.selectedAccount = Account("daiki2kobayashi@gmail.com", "com.linkinpark213.focus")

        this.client = Calendar.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Focus").build()

        val mainButton = findViewById<Button>(R.id.button)

//        startActivityForResult(this.credential!!.newChooseAccountIntent(), REQUEST_ACCOUNTS)

        // EventListeners
        mainButton.setOnClickListener {
            run {
                AsyncGetCalendarListTask(this, this.client!!).execute()
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
                AsyncGetCalendarListTask(this, this.client!!).execute()
            }
            REQUEST_AUTHORIZATION -> {
//                this.startActivityForResult()
                println("Requested authorization")
            }
            REQUEST_READ_CALENDAR -> {
                println("Requested reading calendar")
            }
        }
    }
}

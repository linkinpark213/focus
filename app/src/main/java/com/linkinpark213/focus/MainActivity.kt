package com.linkinpark213.focus

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.linkinpark213.focus.task.AsyncGetCalendarListTask
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    private var updateServiceIntent: Intent = Intent()
    private var windowServiceIntent: Intent = Intent()
    private var focusOn: Boolean = false
    private var ongoingEventTextView: TextView? = null
    private var incomingEventTextView: TextView? = null
    private var mainButton: Button? = null
    private var uiMessageHandler: Handler = Handler {
        when (it.what) {
            MESSAGE_UPDATE_EVENTS -> {
                val data: Bundle = it.data.getBundle("data")!!
                this.ongoingEventTextView!!.text = data.getString("ongoingEventSummary")
                this.incomingEventTextView!!.text = data.getString("incomingEventSummary")
            }
        }
        return@Handler false
    }

    class UIUpdateReceiver(private var mainActivity: MainActivity) : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val message = Message()
            message.what = MainActivity.MESSAGE_UPDATE_EVENTS
            message.data.putBundle("data", p1!!.extras)
            this.mainActivity.uiMessageHandler.sendMessage(message)
        }
    }

    private var uiUpdateReceiver = UIUpdateReceiver(this)
    private var updateIntentFilter = IntentFilter()

    companion object {
        const val REQUEST_ACCOUNTS = 1
        const val REQUEST_AUTHORIZATION = 2
        const val REQUEST_READ_CALENDAR = 3
        const val REQUEST_WINDOW_OVERLAY = 4
        const val MESSAGE_UPDATE_EVENTS = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateIntentFilter.addAction("com.linkinpark213.focus.updateui")
        registerReceiver(uiUpdateReceiver, updateIntentFilter)

        this.windowServiceIntent.action = "com.linkinpark213.service.WINDOW_SERVICE"
        this.windowServiceIntent.`package` = packageName
        this.updateServiceIntent.action = "com.linkinpark213.service.FETCH_EVENTS_SERVICE"
        this.updateServiceIntent.`package` = packageName

        this.mainButton = findViewById<Button>(R.id.button)
        this.ongoingEventTextView = findViewById(R.id.ongoingEventTextView)
        this.incomingEventTextView = findViewById(R.id.incomingEventTextView)

        // Turn on a service that keeps updating UI
        startService(this.updateServiceIntent)

        // EventListeners
        this.mainButton!!.setOnClickListener {
            run {
                if (this.focusOn) {
                    this.focusOn = false
                    it.button.setText(R.string.focus_on_button_text)
                    it.button.background = resources.getDrawable(R.drawable.button_background_on, theme)
                    Toast.makeText(applicationContext, "Focus mode is turned OFF.", Toast.LENGTH_SHORT).show()

                    // Kill floating window
                    stopService(this.windowServiceIntent)
                } else {
                    this.focusOn = true
                    it.button.setText(R.string.focus_off_button_text)
                    it.button.background = resources.getDrawable(R.drawable.button_background_off, theme)

                    // Turn on a service that puts a floating window
                    if (!Settings.canDrawOverlays(this)) {
                        Toast.makeText(
                            applicationContext,
                            "Please permit Focus to display floating windows.",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivityForResult(
                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
                            REQUEST_WINDOW_OVERLAY
                        )
                    } else {
                        startService(this.windowServiceIntent)
                        Toast.makeText(applicationContext, "Focus mode is turned ON.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop updating UI
        stopService(this.updateServiceIntent)
        // Kill floating window
        stopService(this.windowServiceIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        print("Request code: ")
        println(
            when (requestCode) {
                REQUEST_ACCOUNTS -> "REQUEST_ACCOUNTS"
                REQUEST_AUTHORIZATION -> "REQUEST_AUTHORIZATION"
                REQUEST_READ_CALENDAR -> "REQUEST_READ_CALENDAR"
                REQUEST_WINDOW_OVERLAY -> "REQUEST_WINDOW_OVERLAY"
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
            }
            REQUEST_WINDOW_OVERLAY -> {
                println("Requested window overlay")
                if (resultCode == Activity.RESULT_OK) {
                    startService(this.windowServiceIntent)
                    Toast.makeText(applicationContext, "Focus mode is turned ON.", Toast.LENGTH_SHORT).show()
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    if (Settings.canDrawOverlays(this)) {
                        startService(this.windowServiceIntent)
                        Toast.makeText(applicationContext, "Focus mode is turned ON.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

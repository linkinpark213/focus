package com.linkinpark213.focus

import android.accounts.AccountManager
import android.app.Activity
import android.content.*
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.api.client.util.DateTime
import com.linkinpark213.focus.util.TimeFormatter
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    private var updateServiceIntent: Intent = Intent()
    private var windowServiceIntent: Intent = Intent()
    private var monitorServiceIntent: Intent = Intent()
    private var focusOn: Boolean = false
    private var mainButton: Button? = null
    private var uiMessageHandler: Handler = Handler {
        when (it.what) {
            MESSAGE_UPDATE_EVENTS -> {
                // Fetch the data from UI message
                val data: Bundle = it.data.getBundle("data")!!

                // Current time and start/end time of ongoing event
                val currentTime = DateTime(System.currentTimeMillis())

                // Ongoing event
                val ongoingEventSummary = data.getString("ongoingEventSummary")
                findViewById<TextView>(R.id.ongoingEventTextView).text = ongoingEventSummary
                if (ongoingEventSummary != "None") {
                    val ongoingEventStartTime = data.getLong("ongoingEventStartTime")
                    val ongoingEventEndTime = data.getLong("ongoingEventEndTime")
                    val totalTime = ongoingEventEndTime - ongoingEventStartTime
                    val timeSpent = currentTime.value - ongoingEventStartTime
                    // Calculate percentage and length of progress bar
                    val percentage = timeSpent.toFloat() / totalTime.toFloat()
                    val doneBarParams = findViewById<TextView>(R.id.progress_done).layoutParams

                    findViewById<TextView>(R.id.progress_all).setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))

                    doneBarParams.width =
                        (findViewById<TextView>(R.id.progress_all).width.toFloat() * percentage).toInt()

                    findViewById<TextView>(R.id.progress_done).layoutParams = doneBarParams
                    findViewById<TextView>(R.id.ongoingEventTextView).setTextColor(resources.getColor(R.color.colorWhite))

                    // Format and update UI
                    findViewById<TextView>(R.id.ongoingEventTimeTextView).text =
                        TimeFormatter.formatTimePeriodDHM(ongoingEventEndTime - currentTime.value) + " left"
                } else {
                    findViewById<TextView>(R.id.progress_all).setBackgroundColor(resources.getColor(R.color.background_material_light))
                    val doneBarParams = findViewById<TextView>(R.id.progress_done).layoutParams
                    doneBarParams.width = 0
                    findViewById<TextView>(R.id.progress_done).layoutParams = doneBarParams
                    findViewById<TextView>(R.id.ongoingEventTextView).setTextColor(resources.getColor(R.color.abc_secondary_text_material_light))
                    findViewById<TextView>(R.id.ongoingEventTimeTextView).text = ""
                }

                // Incoming event
                val incomingEventSummary = data.getString("incomingEventSummary")
                findViewById<TextView>(R.id.incomingEventTextView).text = data.getString("incomingEventSummary")
                if (incomingEventSummary != "None") {
                    val incomingEventStartTime = data.getLong("incomingEventStartTime")
                    val incomingEventEndTime = data.getLong("incomingEventEndTime")
                    findViewById<TextView>(R.id.incomingEventTimeTextView).text =
                        TimeFormatter.formatTimePeriodDHM(incomingEventStartTime - currentTime.value) + " later"
                } else {
                    findViewById<TextView>(R.id.incomingEventTimeTextView).text = ""
                }

            }
        }
        return@Handler false
    }

    class UIUpdateReceiver(private var uiMessageHandler: Handler) : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val message = Message()
            message.what = MainActivity.MESSAGE_UPDATE_EVENTS
            message.data.putBundle("data", p1!!.extras)
            this.uiMessageHandler.sendMessage(message)
        }
    }

    private var uiUpdateReceiver = UIUpdateReceiver(this.uiMessageHandler)
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

        this.windowServiceIntent.action = "com.linkinpark213.service.WINDOW_SERVICE"
        this.windowServiceIntent.`package` = packageName
        this.updateServiceIntent.action = "com.linkinpark213.service.FETCH_EVENTS_SERVICE"
        this.updateServiceIntent.`package` = packageName
        this.monitorServiceIntent.action = "com.linkinpark213.service.MONITOR_SERVICE"
        this.monitorServiceIntent.`package` = packageName

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(applicationContext) == 0) {
            try {
                val intent = AccountPicker.newChooseAccountIntent(
                    null,
                    null,
                    Array<String>(1) { _ -> GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE },
                    false, null, null, null, null
                )
                startActivityForResult(intent, REQUEST_ACCOUNTS)
            } catch (e: ActivityNotFoundException) {
                println("Activity not found!")
            }
        } else {
            Toast.makeText(applicationContext, "Focus requires Google Play Service.", Toast.LENGTH_LONG).show()
            finish()
        }

        this.mainButton = findViewById<Button>(R.id.button)

        // Turn on a receiver to update UI
        updateIntentFilter.addAction("com.linkinpark213.focus.updateui")
        registerReceiver(uiUpdateReceiver, updateIntentFilter)

        // EventListeners
        this.mainButton!!.setOnClickListener {
            run {
                if (this.focusOn) {
                    this.focusOn = false
                    it.button.setText(R.string.focus_on_button_text)
                    it.button.background = resources.getDrawable(R.drawable.button_background_on, theme)

                    // Kill floating window service
                    stopService(this.windowServiceIntent)
                    // Stop monitor
//                    stopService(this.monitorServiceIntent)
                    sendBroadcast(Intent("com.linkinpark213.focus.stopmonitor"))
                    Toast.makeText(applicationContext, "Focus mode is turned OFF.", Toast.LENGTH_SHORT).show()
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
                        // Start floating window service
                        startService(this.windowServiceIntent)
                        // Start activity monitor service
                        startService(this.monitorServiceIntent)
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
                val accountName = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                this.updateServiceIntent.putExtra("accountName", accountName)
                // Turn on a service that keeps updating UI
                startService(this.updateServiceIntent)
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

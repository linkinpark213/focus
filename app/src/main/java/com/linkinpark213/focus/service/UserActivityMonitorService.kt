package com.linkinpark213.focus.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Message
import com.linkinpark213.focus.FocusReportActivity
import java.util.*
import kotlin.collections.ArrayList

class UserActivityMonitorService : Service() {
    private var focusStartTime: Long = 0
    private var focusEndTime: Long = 0
    private var offTrackTimes = ArrayList<Long>()
    private var backOnTrackTimes = ArrayList<Long>()

    companion object {
        val MESSAGE_CLEAR = 0
        val MESSAGE_START = 1
        val MESSAGE_PAUSE = 2
        val MESSAGE_CONTINUE = 3
        val MESSAGE_STOP = 4
    }

    private val windowMessageHandler = Handler {
        when (it.what) {
            MESSAGE_CLEAR -> {
                this.focusStartTime = 0
                this.focusEndTime = 0
                this.offTrackTimes = ArrayList<Long>()
                this.backOnTrackTimes = ArrayList<Long>()
            }
            MESSAGE_START -> {
                this.focusStartTime = System.currentTimeMillis()
                this.focusEndTime = 0
                this.offTrackTimes = ArrayList<Long>()
                this.offTrackTimes.add(System.currentTimeMillis())
                this.backOnTrackTimes = ArrayList<Long>()
            }
            MESSAGE_PAUSE -> {
                this.offTrackTimes.add(System.currentTimeMillis())
            }
            MESSAGE_CONTINUE -> {
                this.backOnTrackTimes.add(System.currentTimeMillis())
            }
            MESSAGE_STOP -> {
                this.focusEndTime = System.currentTimeMillis()
                println("Focus start time: ${this.focusStartTime}")
                println("Break times:")
                for (time in this.offTrackTimes) {
                    println(time)
                }
                println("Back-on-track times:")
                for (time in this.backOnTrackTimes) {
                    println(time)
                }
                // Calculate total focus time
                val totalFocusTime = System.currentTimeMillis() - this.focusStartTime
                if (offTrackTimes.size > backOnTrackTimes.size) {

                } else {

                }
                val reportIntent = Intent(baseContext, FocusReportActivity::class.java)
                reportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                application.startActivity(reportIntent)
                stopSelf()
            }
        }
        return@Handler false
    }

    class UserActivityReceiver(private val windowMessageHandler: Handler) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.action) {
                "com.linkinpark213.focus.stopmonitor" -> {
                    windowMessageHandler.sendEmptyMessage(MESSAGE_STOP)
                    println("Monitor Stopped!")
                }
                Intent.ACTION_SCREEN_OFF -> {
                    windowMessageHandler.sendEmptyMessage(MESSAGE_CONTINUE)
                    println("User turned off screen!")
                }
                Intent.ACTION_USER_PRESENT -> {
                    windowMessageHandler.sendEmptyMessage(MESSAGE_PAUSE)
                    println("User is active!")
                }
            }
        }
    }

    private val broadcastReceiver = UserActivityReceiver(this.windowMessageHandler)

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.linkinpark213.focus.stopmonitor")
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(this.broadcastReceiver, intentFilter)
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("Events-fetching Service's onBind() called!")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        windowMessageHandler.sendEmptyMessage(MESSAGE_START)
        println("Monitor Started!")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        unregisterReceiver(this.broadcastReceiver)
        super.onDestroy()
    }

}
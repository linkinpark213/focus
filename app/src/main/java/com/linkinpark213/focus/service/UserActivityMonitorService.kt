package com.linkinpark213.focus.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import com.linkinpark213.focus.ReportActivity
import com.linkinpark213.focus.receiver.UserActivityReceiver
import com.linkinpark213.focus.view.FloatingView
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
                val intent = Intent("com.linkinpark213.focus.updatewindow")
                intent.putExtra(
                    "level", when {
                        offTrackTimes.size > 5 -> FloatingView.LEVEL_HIGH
                        offTrackTimes.size > 2 -> FloatingView.LEVEL_MEDIUM
                        else -> FloatingView.LEVEL_NORMAL
                    }
                )
                intent.putExtra("prompt", true)
                sendBroadcast(intent)
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
                val reportIntent = Intent(baseContext, ReportActivity::class.java)
                reportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                reportIntent.putExtra("focusStartTime", this.focusStartTime)
                reportIntent.putExtra("focusEndTime", this.focusEndTime)
                reportIntent.putExtra("backOnTrackTimes", this.backOnTrackTimes.toLongArray())
                reportIntent.putExtra("offTrackTimes", this.offTrackTimes.toLongArray())
                application.startActivity(reportIntent)
                stopSelf()
            }
        }
        return@Handler false
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
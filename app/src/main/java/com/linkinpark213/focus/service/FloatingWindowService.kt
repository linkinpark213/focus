package com.linkinpark213.focus.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import com.linkinpark213.focus.view.FloatingView

class FloatingWindowService : Service() {
    private var mFloatingView: FloatingView? = null
    private var on: Boolean = false

    companion object {
        const val MESSAGE_WINDOW_ON = 0
        const val MASSAGE_WINDOW_OFF = 1
        const val MESSAGE_PROMPT_ON = 2
    }

    private val windowMessageHandler = Handler {
        when (it.what) {
            MESSAGE_WINDOW_ON -> {
                if (!this.on) {
                    this.on = true
                    this.mFloatingView!!.show()
                }
            }
            MASSAGE_WINDOW_OFF -> {
                if (this.on) {
                    this.on = false
                    this.mFloatingView!!.hide()
                }
            }
            MESSAGE_PROMPT_ON -> {

            }
        }
        return@Handler false
    }

    class WindowUpdateReceiver(private val windowMessageHandler: Handler) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.action) {
                "com.linkinpark213.focus.updatewindow" -> {
                    val status = intent.getBooleanExtra("on", false)
                    if (status)
                        this.windowMessageHandler.sendEmptyMessage(FloatingWindowService.MESSAGE_WINDOW_ON)
                    else
                        this.windowMessageHandler.sendEmptyMessage(FloatingWindowService.MASSAGE_WINDOW_OFF)
                }
            }
        }
    }

    private var broadcastReceiver = WindowUpdateReceiver(this.windowMessageHandler)

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.mFloatingView = FloatingView(this)
        val intentFilter = IntentFilter("com.linkinpark213.focus.updatewindow")
        registerReceiver(this.broadcastReceiver, intentFilter)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        this.mFloatingView!!.hide()
        unregisterReceiver(this.broadcastReceiver)
        super.onDestroy()
    }

}
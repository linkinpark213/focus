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

    companion object {
        const val MESSAGE_WINDOW_ON = 0
        const val MASSAGE_WINDOW_OFF = 1
    }

    private val windowMessageHandler = Handler {
        when (it.what) {
            MESSAGE_WINDOW_ON -> {
                this.mFloatingView!!.show()
            }
            MASSAGE_WINDOW_OFF -> {
                this.mFloatingView!!.hide()
            }
        }
        return@Handler false
    }

    class WindowUpdateReceiver(private val windowMessageHandler: Handler) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            this.windowMessageHandler.sendEmptyMessage(FloatingWindowService.MESSAGE_WINDOW_ON)
        }
    }

    private var broadcastReceiver = WindowUpdateReceiver(this.windowMessageHandler)

    override fun onCreate() {
        super.onCreate()
        this.mFloatingView = FloatingView(this)
        val intentFilter = IntentFilter("com.linkinpark213.focus.updatewindow")
        registerReceiver(this.broadcastReceiver, intentFilter)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            this.mFloatingView!!.show()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mFloatingView!!.hide()
    }

}
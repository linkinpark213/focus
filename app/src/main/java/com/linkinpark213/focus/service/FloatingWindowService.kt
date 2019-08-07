package com.linkinpark213.focus.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Message
import com.linkinpark213.focus.view.FloatingView

class FloatingWindowService : Service() {
    private var mFloatingView: FloatingView? = null
    private var on: Boolean = false

    companion object {
        const val MESSAGE_WINDOW_ON = 0
        const val MASSAGE_WINDOW_OFF = 1
        const val MESSAGE_PROMPT_ON = 2
        const val MESSAGE_CHANGE_EMOICON = 3
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
                if (this.on) {
                    this.mFloatingView!!.pop()
                }
            }
            MESSAGE_CHANGE_EMOICON -> {
                if (this.on) {
                    this.mFloatingView!!.changeEmoIcon(it.data.getInt("level"))
                }
            }
        }
        return@Handler false
    }

    class WindowUpdateReceiver(private val windowMessageHandler: Handler) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.action) {
                "com.linkinpark213.focus.triggerwindow" -> {
                    val status = intent.getBooleanExtra("on", false)
                    if (status)
                        this.windowMessageHandler.sendEmptyMessage(FloatingWindowService.MESSAGE_WINDOW_ON)
                    else
                        this.windowMessageHandler.sendEmptyMessage(FloatingWindowService.MASSAGE_WINDOW_OFF)
                }
                "com.linkinpark213.focus.updatewindow" -> {
                    val level = intent.getIntExtra("level", 0)
                    val prompt = intent.getBooleanExtra("prompt", false)
                    val emoIconMessage = Message()
                    emoIconMessage.what = FloatingWindowService.MESSAGE_CHANGE_EMOICON
                    emoIconMessage.data.putInt("level", level)
                    this.windowMessageHandler.sendMessage(emoIconMessage)
                    if (prompt) {
                        val promptMessage = Message()
                        promptMessage.what = FloatingWindowService.MESSAGE_PROMPT_ON
                        promptMessage.data.putInt("level", level)
                        this.windowMessageHandler.sendMessage(promptMessage)
                    }
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
        val intentFilter = IntentFilter("com.linkinpark213.focus.triggerwindow")
        intentFilter.addAction("com.linkinpark213.focus.updatewindow")
        registerReceiver(this.broadcastReceiver, intentFilter)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        this.mFloatingView!!.hide()
        unregisterReceiver(this.broadcastReceiver)
        super.onDestroy()
    }

}
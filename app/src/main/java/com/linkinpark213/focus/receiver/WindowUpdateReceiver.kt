package com.linkinpark213.focus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import com.linkinpark213.focus.service.FloatingWindowService

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
                    promptMessage.what = FloatingWindowService.MESSAGE_WINDOW_POP
                    promptMessage.data.putInt("level", level)
                    this.windowMessageHandler.sendMessage(promptMessage)
                }
            }
        }
    }
}
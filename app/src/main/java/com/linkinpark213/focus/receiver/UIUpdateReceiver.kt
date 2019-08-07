package com.linkinpark213.focus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import com.linkinpark213.focus.MainActivity

class UIUpdateReceiver(private var uiMessageHandler: Handler) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val message = Message()
        message.what = MainActivity.MESSAGE_UPDATE_EVENTS
        message.data.putBundle("data", p1!!.extras)
        this.uiMessageHandler.sendMessage(message)
    }
}

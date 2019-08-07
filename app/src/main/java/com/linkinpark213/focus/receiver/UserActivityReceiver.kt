package com.linkinpark213.focus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.linkinpark213.focus.service.UserActivityMonitorService

class UserActivityReceiver(private val windowMessageHandler: Handler) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent!!.action) {
            "com.linkinpark213.focus.stopmonitor" -> {
                windowMessageHandler.sendEmptyMessage(UserActivityMonitorService.MESSAGE_STOP)
                println("Monitor: Stopped monitor.")
            }
            Intent.ACTION_SCREEN_OFF -> {
                windowMessageHandler.sendEmptyMessage(UserActivityMonitorService.MESSAGE_CONTINUE)
                println("Monitor: User turned off screen.")
            }
            Intent.ACTION_USER_PRESENT -> {
                windowMessageHandler.sendEmptyMessage(UserActivityMonitorService.MESSAGE_PAUSE)
                println("Monitor: User is active.")
            }
        }
    }
}
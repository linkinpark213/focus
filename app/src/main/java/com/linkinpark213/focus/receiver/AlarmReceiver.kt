package com.linkinpark213.focus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.linkinpark213.focus.service.RealTimeUpdateService
import android.support.v4.content.ContextCompat.startForegroundService
import android.os.Build


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p0!!.startForegroundService(Intent(p0, RealTimeUpdateService::class.java))
        } else {
            p0!!.startService(Intent(p0, RealTimeUpdateService::class.java))
        }
    }

}
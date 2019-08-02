package com.linkinpark213.focus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.linkinpark213.focus.service.UpdateUIAlarmService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val intent = Intent(p0, UpdateUIAlarmService::class.java)
        p0!!.startService(intent)
    }

}
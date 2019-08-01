package com.linkinpark213.focus

import android.app.Service
import android.content.Intent
import android.os.IBinder

class FetchEventsService : Service() {
    override fun onCreate() {
        println("Events-fetching Service is ON")
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("Events-fetching Service's onBind() called!")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("Events-fetching Service's onStartCommand() called!")
        return super.onStartCommand(intent, flags, startId)
    }

}
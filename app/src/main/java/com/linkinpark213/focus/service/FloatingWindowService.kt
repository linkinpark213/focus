package com.linkinpark213.focus.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.linkinpark213.focus.view.FloatingView

class FloatingWindowService : Service() {
    private var mFloatingView: FloatingView? = null
    override fun onCreate() {
        super.onCreate()
        this.mFloatingView = FloatingView(this)
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
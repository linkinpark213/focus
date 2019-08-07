package com.linkinpark213.focus.view

import android.content.Context
import android.view.View
import android.view.WindowManager
import java.lang.Exception

class FloatingManager {
    private var mWindowManager: WindowManager? = null
    private var mContext: Context? = null

    private constructor(mContext: Context) {
        this.mContext = mContext
        this.mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    companion object {
        var mInstance: FloatingManager? = null
        fun getInstance(context: Context): FloatingManager {
            if (mInstance == null) {
                mInstance =
                    FloatingManager(context)
            }
            return mInstance as FloatingManager
        }
    }

    fun addView(view: View, params: WindowManager.LayoutParams): Boolean {
        try {
            this.mWindowManager!!.addView(view, params)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun updateView(view: View, params: WindowManager.LayoutParams): Boolean {
        try {
            this.mWindowManager!!.updateViewLayout(view, params)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun removeView(view: View): Boolean {
        try {
            this.mWindowManager!!.removeView(view)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
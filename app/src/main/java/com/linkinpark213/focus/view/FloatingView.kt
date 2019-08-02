package com.linkinpark213.focus.view

import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.linkinpark213.focus.FloatingManager
import com.linkinpark213.focus.R

class FloatingView(context: Context) : FrameLayout(context) {
    private var mContext: Context = context.applicationContext
    private var mLayoutInflater = LayoutInflater.from(context)
    private var mView = mLayoutInflater.inflate(R.layout.floating_view, null)
    private var mImageView = mView.findViewById<ImageView>(R.id.floatingImageView)
    private var mWindowManager = FloatingManager.getInstance(mContext)
    private var mParams: WindowManager.LayoutParams? = null

    init {
        this.mImageView.setImageResource(R.drawable.ic_launcher_foreground)
        this.mImageView.setOnTouchListener(OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = motionEvent.rawX
                    y = motionEvent.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = motionEvent.rawX
                    val nowY = motionEvent.rawY
                    val movedX = (nowX - x)
                    val movedY = (nowY - y)
                    x = nowX
                    y = nowY
                    println("nowX = $nowX, nowY = $nowY, movedX = $movedX, movedY = $movedY")
                    mParams!!.x = (mParams!!.x + movedX).toInt()
                    mParams!!.y = (mParams!!.y + movedY).toInt()

                    this.mWindowManager.updateView(this.mView, this.mParams!!)
                }
            }
            true
        })
    }

    fun show() {
        this.mParams = WindowManager.LayoutParams()
        this.mParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        this.mParams!!.format = PixelFormat.RGBA_8888
        this.mParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        this.mParams!!.width = ViewGroup.LayoutParams.WRAP_CONTENT
        this.mParams!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mWindowManager.addView(this.mView, this.mParams!!)

    }

    fun hide() {
        this.mWindowManager.removeView(this.mView)
    }

}
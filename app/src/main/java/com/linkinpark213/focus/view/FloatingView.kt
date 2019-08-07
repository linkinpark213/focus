package com.linkinpark213.focus.view

import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.view.animation.BounceInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.linkinpark213.focus.FloatingManager
import com.linkinpark213.focus.R
import kotlinx.android.synthetic.main.floating_view.view.*

class FloatingView(context: Context) : FrameLayout(context) {
    private var mContext: Context = context.applicationContext
    private var mLayoutInflater = LayoutInflater.from(context)
    private var mView = mLayoutInflater.inflate(R.layout.floating_view, null)
    private var mImageView = mView.findViewById<ImageView>(R.id.floatingImageView)
    private var mWindowManager = FloatingManager.getInstance(mContext)
    private var mParams: WindowManager.LayoutParams? = null
    private var pulledOver: Boolean = true

    companion object {
        private val edgeDistance = 240.0F
        private val mostInDistance = 100.0F
        const val LEVEL_NORMAL = 0
        const val LEVEL_MEDIUM = 1
        const val LEVEL_HIGH = 2
    }

    init {
        this.mImageView.setImageResource(R.drawable.normal_full)
        this.mImageView.setOnTouchListener(OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = motionEvent.rawX
                    y = motionEvent.rawY
                    pop()
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowY = motionEvent.rawY
                    val movedY = (nowY - y)
                    y = nowY
                    mParams!!.y = (mParams!!.y + movedY).toInt()

                    this.mWindowManager.updateView(this.mView, this.mParams!!)
                }
                MotionEvent.ACTION_UP -> {
                    if (pulledOver) {
                        pop()
                    } else {
                        pull()
                    }
                }
            }
            true
        })
    }

    fun show() {
        this.mParams = WindowManager.LayoutParams()
        this.mParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        this.mParams!!.format = PixelFormat.RGBA_8888
        this.mParams!!.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        this.mParams!!.x = 0
        this.mParams!!.y = 0
        this.mParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        this.mParams!!.width = ViewGroup.LayoutParams.WRAP_CONTENT
        this.mParams!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mWindowManager.addView(this.mView, this.mParams!!)

        this.pull()
    }

    fun pop() {
        this.mView.animate()
            .setInterpolator(BounceInterpolator())
            .setDuration(300)
            .rotation(-30.0F)
            .x(mostInDistance)
            .start()
        this.pulledOver = false
    }

    fun pull() {
        this.mView.animate()
            .setInterpolator(BounceInterpolator())
            .setDuration(300)
            .rotation(0.0F)
            .x(edgeDistance)
            .start()
        this.pulledOver = true
    }

    fun hide() {
        this.mWindowManager.removeView(this.mView)
    }

    fun changeEmoIcon(level: Int) {
        println("LEVEL: $level")
        when (level) {
            LEVEL_NORMAL -> {
                this.mImageView.setImageResource(R.drawable.normal_full)
            }
            LEVEL_MEDIUM -> {
                this.mImageView.setImageResource(R.drawable.angry_full)
            }
            LEVEL_HIGH -> {
                this.mImageView.setImageResource(R.drawable.shotgun_full)
            }
        }
    }

}
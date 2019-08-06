package com.linkinpark213.focus

import android.app.Activity
import android.graphics.*
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.linkinpark213.focus.util.TimeFormatter

class FocusReportActivity() : Activity() {
    class MySurfaceHolderCallback(private val timePeriods: ArrayList<Long>, private val totalTime: Long) :
        SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder?) {
            val canvas = holder!!.lockCanvas()
            val unfocusedPaint = Paint()
            unfocusedPaint.color = Color.parseColor("#D32F2F")
            unfocusedPaint.style = Paint.Style.FILL_AND_STROKE
            val focusedPaint = Paint()
            focusedPaint.color = Color.parseColor("#689F38")
            focusedPaint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawRect(Rect(0, 0, canvas.width, canvas.height), unfocusedPaint)

            var sum = 0L
            for (i in 0 until timePeriods.size) {
                val startX = ((sum.toFloat() / totalTime.toFloat()) * canvas.width.toFloat()).toInt()
                val endX = (((sum + timePeriods[i]).toFloat() / totalTime.toFloat()) * canvas.width.toFloat()).toInt()
                if (i % 2 == 1)
                    canvas.drawRect(Rect(startX, 0, endX, canvas.height), focusedPaint)
                sum += timePeriods[i]
            }
            holder.unlockCanvasAndPost(canvas)
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {

        }

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val bundle = this.intent.extras!!
        val focusStartTime = bundle.getLong("focusStartTime")
        val focusEndTime = bundle.getLong("focusEndTime")
        val backOnTrackTimes = bundle.getLongArray("backOnTrackTimes")!!
        val offTrackTimes = bundle.getLongArray("offTrackTimes")!!
        val totalTime = focusEndTime - focusStartTime
        var offTrackTime = 0L

        val barView = findViewById<LinearLayout>(R.id.focusTimeBarView)
        var current = focusStartTime
        val timePeriods = ArrayList<Long>()
        if (backOnTrackTimes.isEmpty()) {
            timePeriods.add(focusEndTime - focusStartTime)
        } else
            for (i in 0 until backOnTrackTimes.size) {
                timePeriods.add(backOnTrackTimes[i] - current)
                if (i == offTrackTimes.size - 1) {
                    timePeriods.add(focusEndTime - backOnTrackTimes[i])
                    break
                }
                current = offTrackTimes[i + 1]
                timePeriods.add(current - backOnTrackTimes[i])
            }

        val surfaceView = findViewById<SurfaceView>(R.id.focusTimeBarSurfaceView)
        val surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(MySurfaceHolderCallback(timePeriods, totalTime))
        surfaceView.visibility = View.VISIBLE

        var awakenTime = 0L
        for (i in 0 until timePeriods.size step 2) {
            awakenTime += timePeriods[i]
        }

        findViewById<TextView>(R.id.reportTotalTimeValueTextView).text = TimeFormatter.formatTimePeriodDHMS(totalTime)
        findViewById<TextView>(R.id.reportStartTimeValueTextView).text =
            TimeFormatter.formatAbsoluteTime(focusStartTime)
        findViewById<TextView>(R.id.reportEndTimeValueTextView).text = TimeFormatter.formatAbsoluteTime(focusEndTime)
        findViewById<TextView>(R.id.reportTimeAwakenCountValueTextView).text = (offTrackTimes.size - 1).toString()
        findViewById<TextView>(R.id.reportTimeAwakenAmountValueTextView).text =
            TimeFormatter.formatTimePeriodDHMS(awakenTime) + " (" + "%.2f".format(awakenTime.toFloat() / totalTime.toFloat() * 100.0F) + "%)"

    }
}
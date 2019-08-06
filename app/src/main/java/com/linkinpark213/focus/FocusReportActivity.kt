package com.linkinpark213.focus

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.linkinpark213.focus.util.TimeFormatter

class FocusReportActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val bundle = this.intent.extras!!
        val focusStartTime = bundle.getLong("focusStartTime")
        val focusEndTime = bundle.getLong("focusEndTime")
        val backOnTrackTimes = bundle.getLongArray("backOnTrackTimes")!!
        val offTrackTimes = bundle.getLongArray("offTrackTimes")!!
        val totalTime = (focusEndTime - focusStartTime).toFloat()
        var offTrackTime = 0L

        val barView = findViewById<LinearLayout>(R.id.focusTimeBarView)
        var current = focusStartTime
        val timePeriods = ArrayList<Long>()
        for (i in 0 until backOnTrackTimes.size) {
            timePeriods.add(backOnTrackTimes[i] - current)
            if (i == offTrackTimes.size) {
                timePeriods.add(focusEndTime - backOnTrackTimes[i])
                break
            }
            current = offTrackTimes[i]
            timePeriods.add(current - backOnTrackTimes[i])
        }
        for (i in 0 until timePeriods.size) {
            val view = TextView(applicationContext)
            view.layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            if (i % 2 == 1)
                view.setBackgroundColor(resources.getColor(R.color.colorFocused))
            else
                view.setBackgroundColor(resources.getColor(R.color.colorUnfocused))
            val width = (barView.width.toFloat() * (timePeriods[i].toFloat() / totalTime)).toInt()
            barView.addView(view)

            print("Period $i: $timePeriods[i]")
        }

        findViewById<TextView>(R.id.reportTotalTimeValueTextView).text = "abcd"
        findViewById<TextView>(R.id.reportStartTimeValueTextView).text =
            TimeFormatter.formatAbsoluteTime(focusStartTime)
        findViewById<TextView>(R.id.reportEndTimeValueTextView).text = TimeFormatter.formatAbsoluteTime(focusEndTime)
        findViewById<TextView>(R.id.reportTimeAwakenCountValueTextView).text = (offTrackTimes.size - 1).toString()
        findViewById<TextView>(R.id.reportTimeAwakenAmountValueTextView).text = TimeFormatter.formatTimePeriod(0)

    }
}
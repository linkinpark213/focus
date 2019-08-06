package com.linkinpark213.focus.util

import java.text.SimpleDateFormat

class TimeFormatter {
    companion object {
        fun formatTimePeriod(timePeriodMillis: Long): String {
            var minutesLeft = (timePeriodMillis) / 60000
            var hoursLeft = minutesLeft / 60
            var daysLeft = hoursLeft / 24
            hoursLeft %= 24
            minutesLeft %= 60
            var timeLeftString: String = ""
            if (daysLeft > 0) {
                timeLeftString += daysLeft
                timeLeftString += "d "
            }
            if (hoursLeft > 0) {
                timeLeftString += hoursLeft
                timeLeftString += "h "
            }
            timeLeftString += minutesLeft
            timeLeftString += "min"
            return timeLeftString
        }

        fun formatAbsoluteTime(time: Long): String {
            val formatter = SimpleDateFormat("HH:mm:ss")
            return formatter.format(time)
        }
    }
}
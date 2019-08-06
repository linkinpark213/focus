package com.linkinpark213.focus.util

import java.text.SimpleDateFormat

class TimeFormatter {
    companion object {
        private fun toDHMS(timePeriodMillis: Long): ArrayList<Long> {
            var secondsLeft = (timePeriodMillis) / 1000
            var minutesLeft = secondsLeft / 60
            var hoursLeft = minutesLeft / 60
            var daysLeft = hoursLeft / 24
            hoursLeft %= 24
            minutesLeft %= 60
            secondsLeft %= 60
            val dhms = ArrayList<Long>()
            dhms.add(daysLeft)
            dhms.add(hoursLeft)
            dhms.add(minutesLeft)
            dhms.add(secondsLeft)
            return dhms
        }

        fun formatTimePeriodDHMS(timePeriodMillis: Long): String {
            val dhms = toDHMS(timePeriodMillis)
            var timeLeftString: String = ""
            if (dhms[0] > 0) {
                timeLeftString += dhms[0]
                timeLeftString += "d "
            }
            if (dhms[1] > 0) {
                timeLeftString += dhms[1]
                timeLeftString += "h "
            }
            if (dhms[2] > 0) {
                timeLeftString += dhms[2]
                timeLeftString += "min "
            }
            timeLeftString += dhms[3]
            timeLeftString += "s "
            return timeLeftString
        }

        fun formatTimePeriodDHM(timePeriodMillis: Long): String {
            val dhms = toDHMS(timePeriodMillis)
            var timeLeftString: String = ""
            if (dhms[0] > 0) {
                timeLeftString += dhms[0]
                timeLeftString += "d "
            }
            if (dhms[1] > 0) {
                timeLeftString += dhms[1]
                timeLeftString += "h "
            }
            timeLeftString += dhms[2]
            timeLeftString += "min "
            return timeLeftString
        }

        fun formatAbsoluteTime(time: Long): String {
            val formatter = SimpleDateFormat("HH:mm:ss")
            return formatter.format(time)
        }
    }
}
package com.dentify.dentify.apiModel.model

import com.dentify.dentify.util.Util
import java.util.*

class TimeSlot(val startTime: Date, val endTime: Date) {

    fun divide(): List<TimeSlot> {
        val timeSlots = mutableListOf<TimeSlot>()
        var nextStartTime = startTime
        while (true) {
            val toCalendar = Util.dateToCalendar(nextStartTime)
            toCalendar.add(Calendar.MINUTE, 30)
            val nextEndTime = toCalendar.time
            if (nextEndTime.after(endTime)) {
                break
            }
            timeSlots.add(TimeSlot(nextStartTime, nextEndTime))
            nextStartTime = nextEndTime
        }
        return timeSlots
    }

    //proof of case
    //code use in activity/fragment
    fun generateTimeSlots(){

        val startHour = Util.dfParser.parse("2022-04-14T09:00:00")
        val finishHour = Util.dfParser.parse("2022-04-14T17:00:00")

        val timeSlots = TimeSlot(startHour, finishHour).divide()
        val t = timeSlots.get(1)::class.members
        val i = TimeSlot(timeSlots.get(1).startTime, timeSlots.get(1).endTime)::class.members
        var isTrue= false
        if (t.equals(i)){
            isTrue = true
        }
        val r = isTrue
    }
}
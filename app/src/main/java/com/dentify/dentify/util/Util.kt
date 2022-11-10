package com.dentify.dentify.util

import java.text.SimpleDateFormat
import java.util.*

object Util {
    val dfParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val dfParserIsoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val dfClock = SimpleDateFormat("HH:mm")

    fun initials(name: String?): String{
        return name!!
            .split(' ')
            .mapNotNull { it.firstOrNull()?.toString() }
            .reduce { acc, s -> acc + s }
    }

    fun dateToCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }
}
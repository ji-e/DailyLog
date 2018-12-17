package com.example.uohih.dailylog.view

import java.text.SimpleDateFormat
import java.util.*

class CalendarDayInfo {
    private var inMonth: Boolean = false
    private var date: Date? = null


    fun getDay(): String {
        val sdf = SimpleDateFormat("d", Locale.getDefault())
        return sdf.format(date)
    }

    fun isInMonth(): Boolean {
        return inMonth
    }

    fun setInMonth(inMonth: Boolean) {
        this.inMonth = inMonth
    }

    fun getDate(): Date? {
        return date
    }

    fun setDate(date: Date) {
        this.date = date
    }

    fun isSameDay(date1: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = this.date

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}



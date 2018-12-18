package com.example.uohih.dailylog.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.view.CalendarDayInfo
import java.util.*

class CalendarAdapter(private val mContext: Context, private val arrayListDayInfo: ArrayList<CalendarDayInfo>, val date: Date) : BaseAdapter() {
    var selectedDate = date

    override fun getCount(): Int {
        return arrayListDayInfo.size
    }

    override fun getItem(position: Int): Any? {
        return if (position >= count) null else arrayListDayInfo[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val day = arrayListDayInfo[position]
        var convertView = convertView

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_calendar_cell, parent, false)
        }

        val cell = convertView?.findViewById<TextView>(R.id.calendar_cell)
        val today = convertView?.findViewById<ImageView>(R.id.calendar_today)



        /**
         * 날짜 그리기
         */
//        if (day != null) {
            cell?.text = day.getDay()

            if (day.isSameDay(selectedDate)) {
                today?.visibility = View.VISIBLE
            } else {
                today?.visibility = View.INVISIBLE
            }

            if (day.isInMonth()) {
                when {
                    position % 7 + 1 == Calendar.SUNDAY -> cell?.setTextColor(Color.RED)
                    position % 7 + 1 == Calendar.SATURDAY -> cell?.setTextColor(Color.BLUE)
                    else -> cell?.setTextColor(Color.BLACK)
                }
            } else {
                cell?.setTextColor(Color.GRAY)
            }
//        }

        convertView?.tag = day
        return convertView!!

    }

}
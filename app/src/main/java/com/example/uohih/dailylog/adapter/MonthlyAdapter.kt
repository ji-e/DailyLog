package com.example.uohih.dailylog.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.view.CalendarDayInfo
import java.text.SimpleDateFormat
import java.util.*
import android.support.v4.content.ContextCompat


class MonthlyAdapter(private val mContext: Context, private val arrayListDayInfo: ArrayList<CalendarDayInfo>, val date: Date, val logData: ArrayList<DBData>) : BaseAdapter() {
    var selectedDate = date
    private var sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.monthly_item_gridview, parent, false)
        }

        val cell = convertView?.findViewById<TextView>(R.id.monthly_item_cell)
        val today = convertView?.findViewById<ImageView>(R.id.monthly_item_today)
        val layout = convertView?.findViewById<GridLayout>(R.id.monthly_lin)

        layout?.removeAllViews()

        for (i in 0 until logData.size) {
            var calendarCell = sdf.format(arrayListDayInfo[position].getDate())
            if (calendarCell == logData[i].date.toString()) {

                // 이미지뷰 생성
                val iv = ImageView(mContext)
                iv.layoutParams = ViewGroup.LayoutParams(20, 20)

                // 이미지뷰 마진 설정
                val margin = ViewGroup.MarginLayoutParams(iv.layoutParams)
                margin.setMargins(0, 0, 10, 10)
                iv.layoutParams = GridLayout.LayoutParams(margin)

                // 이미지뷰 색 변경
                val roundDrawable = ContextCompat.getDrawable(mContext, R.drawable.circle)
                when (i % 6) {
                    0 -> roundDrawable?.setColorFilter(ContextCompat.getColor(mContext, R.color.c_b384d4), PorterDuff.Mode.SRC_ATOP)
                    1 -> roundDrawable?.setColorFilter(ContextCompat.getColor(mContext, R.color.c_fdc480), PorterDuff.Mode.SRC_ATOP)
                    2 -> roundDrawable?.setColorFilter(ContextCompat.getColor(mContext, R.color.c_a1dee9), PorterDuff.Mode.SRC_ATOP)
                    3 -> roundDrawable?.setColorFilter(ContextCompat.getColor(mContext, R.color.c_cee59a), PorterDuff.Mode.SRC_ATOP)
                    4 -> roundDrawable?.setColorFilter(ContextCompat.getColor(mContext, R.color.c_cba8de), PorterDuff.Mode.SRC_ATOP)
                    5 -> roundDrawable?.setColorFilter(ContextCompat.getColor(mContext, R.color.c_9dabe2), PorterDuff.Mode.SRC_ATOP)
                }
                iv.setBackgroundDrawable(roundDrawable)


                // 이미지뷰 추가
                layout?.addView(iv)
            }
        }


        /**
         * 날짜 그리기
         */
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


        convertView?.tag = day

        return convertView!!

    }


}
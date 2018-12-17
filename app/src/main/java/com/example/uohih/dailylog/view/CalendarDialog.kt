package com.example.uohih.dailylog.view

import android.app.Activity
import android.app.Dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import kotlinx.android.synthetic.main.dialog_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarDialog(context: Context, theme: Int) : Dialog(context, theme) {

    class Builder(private val context: Context) {
        lateinit var dialogTitle: String
        private var close = false

        lateinit var mClosebtnClickListener: DialogInterface.OnClickListener
        lateinit var mItemClickListener: AdapterView.OnItemClickListener

        lateinit var gridAdapter: GridAdapter
        lateinit var gridView: GridView

        private val todayJson = DLogBaseActivity().getToday()

        val instance = Calendar.getInstance()
        var sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        lateinit var arrayListDayInfo: ArrayList<CalendarDayInfo>
        private var selectedDate: Date = Date()


        /**
         * 닫기 버튼 리스너
         */
        fun setmCloseBtnClickListener(text: String, mClosebtnClickListener: DialogInterface.OnClickListener): Builder {
            if (mClosebtnClickListener != null) {
                close = true
            }
            dialogTitle = text
            this.mClosebtnClickListener = mClosebtnClickListener
            return this
        }

        /**
         * 그리드 뷰 아이템 리스너
         */
        fun setmItemClickListener(mItemClickListener: AdapterView.OnItemClickListener): Builder {
            this.mItemClickListener = mItemClickListener
            return this
        }

        /**
         * 그리드 뷰
         */
        fun setGridAdapter(gridAdapter: GridAdapter): Builder {
            this.gridAdapter = gridAdapter
            return this
        }


        /**
         * 선택 날짜
         */

        private fun setSelectedDate(date: Date?) {
            selectedDate = date!!

            if (gridAdapter != null) {
                gridAdapter.selectedDate = date
            }
        }


        private fun getCalendar(dateForCurrentMonth: Date) {
            var dayOfWeek: Int
            val thisMonthLastDay: Int

            arrayListDayInfo.clear()

            val calendar = Calendar.getInstance()
            calendar.time = dateForCurrentMonth

            calendar.set(Calendar.DATE, 1)//1일로 변경
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)//1일의 요일 구하기
            LogUtil.d("dayOfWeek = $dayOfWeek")

            if (dayOfWeek == Calendar.SUNDAY) { //현재 달의 1일이 무슨 요일인지 검사
                dayOfWeek += 7
            }

            thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)


            var day: CalendarDayInfo

            calendar.add(Calendar.DATE, -1 * (dayOfWeek - 1)) //현재 달력화면에서 보이는 지난달의 시작일
            for (i in 0 until dayOfWeek - 1) {
                day = CalendarDayInfo()
                day.setDate(calendar.time)
                day.setInMonth(false)
                arrayListDayInfo.add(day)

                calendar.add(Calendar.DATE, +1)
            }

            for (i in 1..thisMonthLastDay) {
                day = CalendarDayInfo()
                day.setDate(calendar.time)
                day.setInMonth(true)
                arrayListDayInfo.add(day)

                calendar.add(Calendar.DATE, +1)
            }

            for (i in 1 until 42 - (thisMonthLastDay + dayOfWeek - 1) + 1) {
                day = CalendarDayInfo()
                day.setDate(calendar.time)
                day.setInMonth(false)
                arrayListDayInfo.add(day)

                calendar.add(Calendar.DATE, +1)
            }

            gridAdapter = GridAdapter(context, arrayListDayInfo, selectedDate)
            gridView.adapter = gridAdapter

        }


        /**
         * 커스텀 다이얼로그 생성
         */
        fun create(): CalendarDialog {
            val dialog = CalendarDialog((context as Activity), android.R.style.Theme_Material_Dialog_MinWidth)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_calendar, null)

            dialog.addContentView(contentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            arrayListDayInfo = ArrayList<CalendarDayInfo>()
            val mThisMonthCalendar = Calendar.getInstance()

            /**
             * 현재 날짜 세팅
             */
            contentView.calendar_tv_year.text = todayJson.getString("year")
            contentView.calendar_tv_month.text = todayJson.getString("month")


            /**
             * 닫기 버튼
             */
            contentView.calendar_btn_close.setOnClickListener {
                dialog.dismiss()
            }

            /**
             * 년 이전버튼
             */
            contentView.calendar_btn_backy.setOnClickListener {
                instance.add(Calendar.YEAR, -1)
                getCalendar(instance.time)
                contentView.calendar_tv_year.text = instance.get(Calendar.YEAR).toString()
            }

            /**
             * 년 다음버튼
             */
            contentView.calendar_btn_nexty.setOnClickListener {
                if (contentView.calendar_tv_year.text.toString().toInt() < todayJson.getString("year").toInt()) {
                    instance.add(Calendar.YEAR, +1)
                    getCalendar(instance.time)
                    contentView.calendar_tv_year.text = instance.get(Calendar.YEAR).toString()
                }
            }


            /**
             * 월 이전버튼
             */
            contentView.calendar_btn_backm.setOnClickListener {
                instance.add(Calendar.MONTH, -1)
                getCalendar(instance.time)
                contentView.calendar_tv_month.text = String.format("%02d", instance.get(Calendar.MONTH) + 1)
            }

            /**
             * 월 다음버튼
             */
            contentView.calendar_btn_nextm.setOnClickListener {
                if (contentView.calendar_tv_month.text.toString().toInt() < todayJson.getString("month").toInt()) {
                    instance.add(Calendar.MONTH, +1)
                    getCalendar(instance.time)
                    contentView.calendar_tv_month.text = String.format("%02d", instance.get(Calendar.MONTH) + 1)
                }
            }

            /**
             * 날짜 클릭
             */
            contentView.calendar_gridview.setOnItemClickListener { parent, view, position, id ->

                val selectedDate=(view.tag as CalendarDayInfo).getDate()
                if((sdf.format(selectedDate)).toInt()<=todayJson.get("yyyymmdd").toString().toInt()) {
                    setSelectedDate(selectedDate)
                    gridAdapter.notifyDataSetChanged()
                }

            }

            /**
             * 확인버튼 클릭
             */
            contentView.calendar_btn_confirm.setOnClickListener {
                val base = DLogBaseApplication()
                base.setSeleteDate(sdf.format(selectedDate))
                dialog.dismiss()
            }

            // 그리드뷰 세팅
            gridView = contentView.calendar_gridview
//            gridView.adapter = gridAdapter
//            gridView.onItemClickListener = mItemClickListener
            getCalendar(mThisMonthCalendar.time)
            setSelectedDate(Date())
            return dialog
        }

    }


    /**
     * 캘린더 다이얼로그
     */
    fun showDialogCalendar(context: Context): CalendarDialog? {
        if (context == null) {
            return null
        }

        if (context is Activity) {
            val activity = context as Activity?

            if (activity!!.isFinishing || activity.isDestroyed) {
                return null
            }
        }


        val builder = CalendarDialog.Builder(context)
        val dialog = builder.create()
        return dialog
    }


}

class GridAdapter(private val mContext: Context, val arrayListDayInfo: ArrayList<CalendarDayInfo>, val date: Date) : BaseAdapter() {
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
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_calendar_cell, parent, false)

//            convertView.tag = holder
        } else {
//            holder = convertView.tag as ViewHolder
        }
        val cell = convertView?.findViewById<TextView>(R.id.calendar_cell)
        val today = convertView?.findViewById<ImageView>(R.id.calendar_today)

        if (day != null) {
            val tvDay = cell
            tvDay?.text = day.getDay()

            val ivSelected = today
            if (day.isSameDay(selectedDate)) {
                ivSelected?.visibility = View.VISIBLE
            } else {
                ivSelected?.visibility = View.INVISIBLE
            }

            if (day.isInMonth()) {
                if (position % 7 + 1 == Calendar.SUNDAY) {
                    tvDay?.setTextColor(Color.RED)
                } else if (position % 7 + 1 == Calendar.SATURDAY) {
                    tvDay?.setTextColor(Color.BLUE)
                } else {
                    tvDay?.setTextColor(Color.BLACK)
                }
            } else {
                tvDay?.setTextColor(Color.GRAY)
            }


        }

        convertView?.tag = day
        return convertView!!

    }

    internal inner class ViewHolder {
        lateinit var cell: TextView
        lateinit var today: ImageView
    }
}




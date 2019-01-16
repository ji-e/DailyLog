package com.example.uohih.dailylog.main

import android.os.Bundle
import android.view.View
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.adapter.DBData
import com.example.uohih.dailylog.adapter.MonthlyAdapter
import com.example.uohih.dailylog.adapter.SearchAdapter
import com.example.uohih.dailylog.base.BackPressCloseHandler
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.view.CalendarDayInfo
import kotlinx.android.synthetic.main.activity_monthly.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 월간일지
 */
class MonthlyActivity : DLogBaseActivity() {
    private val base = DLogBaseApplication()
    private var jsonCalendar = JSONObject(getToday().toString())
    private val db = DBHelper(this)
    private val currentDate = jsonCalendar.get("yyyymmdd").toString()


    private var arrayListDayInfo: ArrayList<CalendarDayInfo> = ArrayList<CalendarDayInfo>()
    lateinit var monthlyAdapter: MonthlyAdapter
    private var selectedDate: Date = Date()
    private val mThisMonthCalendar = Calendar.getInstance()

    private val instance = Calendar.getInstance()
    private var sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())


    lateinit var mAadapter: SearchAdapter
    private var dayList = arrayListOf<DBData>()

    // back key exit
    private lateinit var  backPressCloseHandler:BackPressCloseHandler

    // 리스트 뷰
    var monthlyList = arrayListOf<DBData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly)

        // back key exit 초기화
        backPressCloseHandler = BackPressCloseHandler(this)

        // 상단 바 캘린더 클릭 이벤트
        monthly_title_view.setCalendarBtnClickListener(View.OnClickListener {
            base.setDateInfom(jsonCalendar)
            setData(currentDate.substring(0,6))
            // 현재 날짜 세팅
            monthly_tv_date.text = String.format(getString(R.string.monthly_date), jsonCalendar.get("year"), jsonCalendar.get("month"))
            monthly_date.text=jsonCalendar.get("yyyymmdd").toString()

            // 그리드뷰 세팅
            getCalendar(mThisMonthCalendar.time)
            setSelectedDate(Date())
            getDayList()
        })



        // 이전 버튼 클릭 이벤트
        monthly_btn_back.setOnClickListener {
            var preCalendar = JSONObject(getDate(true, 1, "월").toString())
            base.setDateInfom(preCalendar)
            monthly_tv_date.text = String.format(getString(R.string.monthly_date), preCalendar.get("year"), preCalendar.get("month"))
            instance.add(Calendar.MONTH, -1)
            getCalendar(instance.time)
            setData(preCalendar.get("year").toString() + preCalendar.get("month").toString())

        }

        // 다음 버튼 클릭 이벤트
        monthly_btn_next.setOnClickListener {
            var nextCalendar = JSONObject(getDate(false, 1, "월").toString())
            if (currentDate.substring(0,6) >= nextCalendar.get("yyyymmdd").toString().substring(0,6)) {
                base.setDateInfom(nextCalendar)
                monthly_tv_date.text = String.format(getString(R.string.monthly_date), nextCalendar.get("year"), nextCalendar.get("month"))
                instance.add(Calendar.MONTH, +1)
                getCalendar(instance.time)
                setData(nextCalendar.get("year").toString() + nextCalendar.get("month").toString())

            }
        }


        /**
         * 날짜 클릭
         */
        monthly_gridview.setOnItemClickListener { parent, view, position, id ->
            val selectedDate = (view.tag as CalendarDayInfo).getDate()
            if ((sdf.format(selectedDate)).toInt() <= currentDate.toInt()) {
                setSelectedDate(selectedDate)
                monthlyAdapter.notifyDataSetChanged()
//                monthly_date.text=sdf.format(selectedDate)
                getDayList()
            base.setDateInfom( DLogBaseActivity().getToday(sdf.format(selectedDate)))
            }
        }
    }

    /**
     * DB에서 데이터 가져와 set
     * date: String
     */
    private fun setData(date: String) {
        val cursor = db.select((date + "01").toInt(), (date + "31").toInt())
        monthlyList.clear()
        while (cursor.moveToNext()) {
            monthlyList.add(DBData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
        }
        monthlyAdapter.notifyDataSetChanged()
    }

    /**
     * on Resume
     */
    override fun onResume() {
        LogUtil.d("onResume")
        super.onResume()

        /**
         * 프리퍼런스에 액티비티 상태 저장
         */
        setPreference(activitySetting, "monthly")

        jsonCalendar = JSONObject(base.getDateInfom().toString())
        mThisMonthCalendar.set(Calendar.YEAR,jsonCalendar.get("year").toString().toInt())
        mThisMonthCalendar.set(Calendar.MONTH,jsonCalendar.get("month").toString().toInt()-1)
        mThisMonthCalendar.set(Calendar.DAY_OF_MONTH,jsonCalendar.get("date").toString().toInt())
        instance.set(Calendar.YEAR,jsonCalendar.get("year").toString().toInt())
        instance.set(Calendar.MONTH,jsonCalendar.get("month").toString().toInt()-1)
        instance.set(Calendar.DAY_OF_MONTH,jsonCalendar.get("date").toString().toInt())
        // 오늘날짜 세팅
        monthly_date.text=jsonCalendar.get("yyyymmdd").toString()
        // 현재 날짜 세팅
        monthly_tv_date.text = String.format(getString(R.string.monthly_date), jsonCalendar.get("year"), jsonCalendar.get("month"))


        // 그리드뷰 세팅
        getCalendar(mThisMonthCalendar.time)
//        setSelectedDate(Date())
        setData(monthly_tv_date.text.toString().substring(0,4)+monthly_tv_date.text.toString().substring(6,8))

        mAadapter = SearchAdapter(this, dayList,false)
        monthly_listview.adapter = mAadapter
        getDayList()

        DLogBaseApplication().setMonthly(true)
    }


    /**
     * 캘린더 가져오기
     * dateForCurrentMonth: Date
     */
    private fun getCalendar(dateForCurrentMonth: Date) {
        var dayOfWeek: Int
        val thisMonthLastDay: Int

        arrayListDayInfo.clear()

        val calendar = Calendar.getInstance()
        calendar.time = dateForCurrentMonth

        calendar.set(Calendar.DATE, 1)//1일로 변경
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)//1일의 요일 구하기
        LogUtil.d("dayOfWeek = $dayOfWeek")


        //현재 달의 1일이 무슨 요일인지 검사
        if (dayOfWeek == Calendar.SUNDAY) {
            dayOfWeek += 7
        }

        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)


        var day: CalendarDayInfo

        calendar.add(Calendar.DATE, -1 * (dayOfWeek - 1))
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

        monthlyAdapter = MonthlyAdapter(this, arrayListDayInfo, selectedDate, monthlyList)
        monthly_gridview.adapter = monthlyAdapter

    }

    /**
     * 현재 날짜 동그라미
     * date: Date?
     */
    private fun setSelectedDate(date: Date?) {
        selectedDate = date!!

        if (monthlyAdapter != null) {
            monthlyAdapter.selectedDate = date
        }
        monthly_date.text=sdf.format(selectedDate)
    }

    /**
     * dayList 가져오기
     */
    fun getDayList() {
        var cursor =db.select(monthly_date.text.toString().toInt())

        dayList.clear()
        while (cursor.moveToNext()) {
            dayList.add(DBData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
        }

        mAadapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        DLogBaseApplication().setMonthly(false)
    }

    /**
     * Back Key
     */
    override fun onBackPressed() {
//        super.onBackPressed()
      backPressCloseHandler.onBackPressed()
    }

}

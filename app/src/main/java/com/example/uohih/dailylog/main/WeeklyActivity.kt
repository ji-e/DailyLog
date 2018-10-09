package com.example.uohih.dailylog.main

import android.os.Bundle
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.database.DBHelper
import kotlinx.android.synthetic.main.activity_weekly.*
import org.json.JSONObject

class WeeklyActivity : DLogBaseActivity() {

    private val tag = "WeeklyActivity"
    private var jsonCalendar = JSONObject(getToday().toString())
    private val db = DBHelper(this)
    private val currentDate = jsonCalendar.get("yyyymmdd").toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly)

        // 현재 날짜 세팅
        weekly_tv_date.text = String.format(getString(R.string.weekly_date), jsonCalendar.get("year"), jsonCalendar.get("month"), jsonCalendar.get("week"))
        DLogBaseApplication().setDateInfom(jsonCalendar)

        // 이전 버튼 클릭 이벤트
        weekly_btn_back.setOnClickListener {
            var preCalendar = JSONObject(getDate(true, 1, "주").toString())
            DLogBaseApplication().setDateInfom(preCalendar)
            weekly_tv_date.text = String.format(getString(R.string.weekly_date), preCalendar.get("year"), preCalendar.get("month"), preCalendar.get("week"))
//            setData(preCalendar.get("yyyymmdd").toString())
        }

        // 다음 버튼 클릭 이벤트
        weekly_btn_next.setOnClickListener {
            var nextCalendar = JSONObject(getDate(false, 1, "주").toString())
            DLogBaseApplication().setDateInfom(nextCalendar)
            weekly_tv_date.text = String.format(getString(R.string.weekly_date), nextCalendar.get("year"), nextCalendar.get("month"), nextCalendar.get("week"))
//            setData(nextCalendar.get("yyyymmdd").toString())
        }
    }
}

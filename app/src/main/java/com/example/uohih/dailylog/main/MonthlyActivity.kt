package com.example.uohih.dailylog.main

import android.os.Bundle
import android.util.Log
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.database.DBHelper
import kotlinx.android.synthetic.main.activity_monthly.*
import org.json.JSONObject

class MonthlyActivity : DLogBaseActivity() {

    private val tag = "MonthlyActivity"
    private var jsonCalendar = JSONObject(getToday().toString())
    private val db = DBHelper(this)
    private val currentDate = jsonCalendar.get("yyyymmdd").toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly)

        // 현재 날짜 세팅
        monthly_tv_date.text = String.format(getString(R.string.monthly_date), jsonCalendar.get("year"), jsonCalendar.get("month"))

        // 이전 버튼 클릭 이벤트
        monthly_btn_back.setOnClickListener {
            var preCalendar = JSONObject(getDate(true, 1, "월").toString())
            DLogBaseApplication().setDateInfom(preCalendar)
            monthly_tv_date.text = String.format(getString(R.string.monthly_date), preCalendar.get("year"), preCalendar.get("month"))
//            setData(preCalendar.get("yyyymmdd").toString())


            Log.d(tag, "11111111111111111111111111111111")
        }

        // 다음 버튼 클릭 이벤트
        monthly_btn_next.setOnClickListener {
            var nextCalendar = JSONObject(getDate(false, 1, "월").toString())
            DLogBaseApplication().setDateInfom(nextCalendar)
            monthly_tv_date.text = String.format(getString(R.string.monthly_date), nextCalendar.get("year"), nextCalendar.get("month"))
//            setData(nextCalendar.get("yyyymmdd").toString())
            Log.d(tag, "222222222222222222222222222222222")
        }
    }


//    private fun setData(date: String) {
//        val cursor = db.select(date.toInt())
//        dailyList.clear()
//        while (cursor.moveToNext()) {
//            dailyList.add(DailyData(cursor.getString(2), cursor.getString(3)))
//        }
//
//        val mAadapter = DailyRvAadapter(this, dailyList)
//
//        daily_recyclerView.adapter = mAadapter
//
//        val lm = LinearLayoutManager(this)
//        daily_recyclerView.layoutManager = lm
//        daily_recyclerView.setHasFixedSize(true)
//    }
}

package com.example.uohih.dailylog.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.database.DBHelper
import kotlinx.android.synthetic.main.activity_daily.*
import kotlinx.android.synthetic.main.activity_write.*
import org.json.JSONObject

class WriteActivity : DLogBaseActivity() {
    private val tag = "WriteActivity"
    private var jsonCalendar = JSONObject(getToday().toString())
    private val currentDate = jsonCalendar.get("yyyymmdd").toString()
    private val db = DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        // 현재 날짜 세팅
        this.write_tv_date.text = String.format(getString(R.string.daily_date), jsonCalendar.get("year"), jsonCalendar.get("month"), jsonCalendar.get("date"), jsonCalendar.get("day"))
        setDateInfom(jsonCalendar)
        // 일지 쓰기 버튼 클릭 이벤트
        write_btn_write.setOnClickListener {

            Log.d(tag, write_et_title.text.toString())
            if (write_et_title.text.toString().isEmpty()) {
                emptyAlert()
            } else {
                db.insert(currentDate.toInt(), write_et_title.text.toString(), write_et_content.text.toString())
                finish()
            }
        }

        // 이전 버튼 클릭 이벤트
        write_btn_back.setOnClickListener {
            var preCalendar = JSONObject(getDate(true, 1).toString())
            setDateInfom(preCalendar)
            write_tv_date.text = String.format(getString(R.string.daily_date), preCalendar.get("year"), preCalendar.get("month"), preCalendar.get("date"), preCalendar.get("day"))
            write_et_title.setText("")
            write_et_content.setText("")
            Log.d(tag, "11111111111111111111111111111111")
        }

        // 다음 버튼 클릭 이벤트
        write_btn_next.setOnClickListener {
            var nextCalendar = JSONObject(getDate(false, 1).toString())
            setDateInfom(nextCalendar)
            write_tv_date.text = String.format(getString(R.string.daily_date), nextCalendar.get("year"), nextCalendar.get("month"), nextCalendar.get("date"), nextCalendar.get("day"))
            write_et_title.setText("")
            write_et_content.setText("")
            Log.d(tag, "222222222222222222222222222222222")
        }

    }

    /**
     * 제목은 null이면 안됨
     */
    private fun emptyAlert() {
        //todo alert 일단 toast
        Toast.makeText(this, "제목 써!", Toast.LENGTH_SHORT).show()
    }
}

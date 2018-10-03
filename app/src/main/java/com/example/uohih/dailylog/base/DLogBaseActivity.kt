package com.example.uohih.dailylog.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import org.json.JSONObject
import java.util.*

open class DLogBaseActivity : Activity() {

    var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = applicationContext
//        setContentView(R.layout.activity_intro)
    }

    override fun onResume() {
        super.onResume()
    }


    /**
     * 현재 날짜 구하기
     */
    fun getToday(): JSONObject {
        var jsonCalendar = JSONObject()
        val instance = Calendar.getInstance()
        val year = instance.get(Calendar.YEAR).toString() //현재 년도
        var month = (instance.get(Calendar.MONTH) + 1).toString() //현재 월
        var date = instance.get(Calendar.DAY_OF_MONTH).toString() //현재 날짜
        val week = instance.get(Calendar.WEEK_OF_MONTH).toString() //현재 월의 주
        var day = instance.get(Calendar.DAY_OF_WEEK).toString() //현재 요일


        // 한자리수 앞에 0표기
        if (month.toInt() < 10) month = "0$month"
        if (date.toInt() < 10) date = "0$date"

        // 요일로 변환
        when (day) {
            "1" -> day = "일"
            "2" -> day = "월"
            "3" -> day = "화"
            "4" -> day = "수"
            "5" -> day = "목"
            "6" -> day = "금"
            "7" -> day = "토"

        }

        jsonCalendar.put("year", year)
        jsonCalendar.put("month", month)
        jsonCalendar.put("date", date)
        jsonCalendar.put("week", week)
        jsonCalendar.put("day", day)
        jsonCalendar.put("yyyymmdd", "$year$month$date")

        return jsonCalendar
    }

    /**
     * 날짜 구하기
     * pre: true: 이전 날짜 / false: 다음 날짜
     * num: 구할 날짜의 차이
     * type: 일, 주, 월
     */
    fun getDate(pre: Boolean, num: Int, type: String): JSONObject {
        var jsonCalendar = JSONObject(DLogBaseApplication().getDateInfom().toString())
        var year = jsonCalendar.get("year").toString().toInt()// 년도
        var month = jsonCalendar.get("month").toString().toInt()// 월
        var date = jsonCalendar.get("date").toString().toInt()// 날짜
        var week = jsonCalendar.get("week").toString().toInt() // 월의 주
        var day = jsonCalendar.get("day").toString()// 요일
        val cal = GregorianCalendar(year, month - 1, date)
        if (pre) {
            when (type) {
                "일" -> cal.add(Calendar.DAY_OF_YEAR, -num) // 이전 일
                "주" -> cal.add(Calendar.WEEK_OF_MONTH, -num) // 이전 일
                "월" -> cal.add(Calendar.MONTH, -num) // 이전 달
            }


//            when (day) {
//                "화" -> day = "월"
//                "수" -> day = "화"
//                "목" -> day = "수"
//                "금" -> day = "목"
//                "토" -> day = "금"
//                "일" -> day = "토"
//                "월" -> day = "일"
//
//            }
        } else {
            when (type) {
                "일" -> cal.add(Calendar.DAY_OF_YEAR, +num) // 이후 일
                "주" -> cal.add(Calendar.WEEK_OF_MONTH, +num) // 이후 일
                "월" -> cal.add(Calendar.MONTH, +num) // 이후 달
            }

//            when (day) {
//                "일" -> day = "월"
//                "월" -> day = "화"
//                "화" -> day = "수"
//                "수" -> day = "목"
//                "목" -> day = "금"
//                "금" -> day = "토"
//                "토" -> day = "일"
//
//            }
        }


        year = cal.get(Calendar.YEAR)
        month = (cal.get(Calendar.MONTH)) + 1
        date = cal.get(Calendar.DAY_OF_MONTH)
        week = cal.get(Calendar.WEEK_OF_MONTH)
        day = cal.get(Calendar.DAY_OF_WEEK).toString()

        // 한자리수 앞에 0표기
        var _month = month.toString()
        var _date = date.toString()
        if (month < 10) _month = "0$month"
        if (date < 10) _date = "0$date"

        // 요일로 변환
        when (day) {
            "1" -> day = "일"
            "2" -> day = "월"
            "3" -> day = "화"
            "4" -> day = "수"
            "5" -> day = "목"
            "6" -> day = "금"
            "7" -> day = "토"

        }


        jsonCalendar.put("year", year.toString())
        jsonCalendar.put("month", _month)
        jsonCalendar.put("date", _date)
        jsonCalendar.put("week", week.toString())
        jsonCalendar.put("day", day)
        jsonCalendar.put("yyyymmdd", "$year$_month$_date")

        Log.d("calcal", jsonCalendar.get("yyyymmdd").toString())
        return jsonCalendar
    }


    override fun onDestroy() {
        super.onDestroy()

//        val db = DBHelper(this)
//        db.close()
    }

    override fun onPause() {
        super.onPause()
    }

}

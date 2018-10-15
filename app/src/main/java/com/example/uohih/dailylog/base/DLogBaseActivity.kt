package com.example.uohih.dailylog.base

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Handler
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


open class DLogBaseActivity : Activity() {

    var mContext: Context? = null

    val passwordCheck = 0
    val activitySetting = "activitySetting"
    val passwordSetting = "passwordSetting"
    val temp = "temp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = applicationContext
//        setContentView(R.layout.activity_intro)
    }


    /**
     * 현재 날짜 구하기
     */
    fun getToday(): JSONObject {
        return getToday(null)
    }

    /**
     * 현재 날짜 구하기
     */
    fun getToday(date: String?): JSONObject {
        var jsonCalendar = JSONObject()
        val instance = Calendar.getInstance()
        if (date != null) {
            instance.set(date.substring(0, 4).toInt(), date.substring(4, 6).toInt() - 1, date.substring(6).toInt())
        }
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
        return getDate(pre, num, type, DLogBaseApplication().getDateInfom())
    }

    fun getDate(pre: Boolean, num: Int, type: String, jsonObject: JSONObject): JSONObject {
        var jsonCalendar = JSONObject(jsonObject.toString())
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
        } else {
            when (type) {
                "일" -> cal.add(Calendar.DAY_OF_YEAR, +num) // 이후 일
                "주" -> cal.add(Calendar.WEEK_OF_MONTH, +num) // 이후 일
                "월" -> cal.add(Calendar.MONTH, +num) // 이후 달
            }
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

        LogUtil.d(jsonCalendar.get("yyyymmdd").toString())
        return jsonCalendar
    }


    /**
     * 주차 구하기
     */
    fun getWeek(date: String): String {
        val instance = Calendar.getInstance()
        instance.set(date.substring(0, 4).toInt(), date.substring(4, 6).toInt() - 1, date.substring(6).toInt())
        val week = instance.get(Calendar.WEEK_OF_MONTH).toString()
        return week
    }

    /**
     * 요일 구하기
     */
    fun getDay(date: String): String {
        val instance = Calendar.getInstance()
        instance.set(date.substring(0, 4).toInt(), date.substring(4, 6).toInt() - 1, date.substring(6).toInt())
        var day = instance.get(Calendar.DAY_OF_WEEK).toString()
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
        return day
    }

    /**
     * 주의 첫날과 마지막날 구하기
     * 1: 월- 7: 일
     */
    fun weekCalendar(cur: String): ArrayList<String> {
        val arrayList = ArrayList<String>(7)
        val cal = Calendar.getInstance()
        cal.set(cur.substring(0, 4).toInt(), cur.substring(4, 6).toInt() - 1, cur.substring(6).toInt())
        val inYear = cal.get(Calendar.YEAR)
        val inMonth = cal.get(Calendar.MONTH)
        var inDay = cal.get(Calendar.DAY_OF_MONTH)
        var yoil = cal.get(Calendar.DAY_OF_WEEK) //요일나오게하기(숫자로)
//        if (yoil != 1) {   //해당요일이 일요일이 아닌경우
//            yoil = yoil - 2
//        } else {           //해당요일이 일요일인경우
//            yoil = 7
//            yoil = yoil - 2
//        }
        inDay -= yoil
        for (i in 0..6) {
            cal.set(inYear, inMonth, inDay + i + 1)  //
            val y = Integer.toString(cal.get(Calendar.YEAR))
            var m = Integer.toString(cal.get(Calendar.MONTH) + 1)
            var d = Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
            if (m.length == 1) m = "0$m"
            if (d.length == 1) d = "0$d"

            arrayList.add(y + m + d)
        }
        return arrayList
    }

    /**
     * 앱 버전 정보 가져오기
     */
    fun getVersionInfo(): String {
        val info: PackageInfo? = mContext?.packageManager?.getPackageInfo(mContext?.packageName, 0)
        return info?.versionName.toString()
    }

    /**
     * 키패드 데이터 세팅
     */
    fun setkeyPadData(): ArrayList<String> {
        val arrayList = ArrayList<String>()
        for (i in 0 until 12) {
            when (i) {
                9 -> arrayList.add("왼")
                10 -> arrayList.add("0")
                11 -> arrayList.add("오")
                else -> arrayList.add((i + 1).toString())
            }
        }
        return arrayList
    }

    /**
     * 프리퍼런스에 값 저장
     */
    fun setPreference(key: String, str: String) {
        val pref = getSharedPreferences(key, MODE_PRIVATE)
        var editor = pref.edit()
        editor.putString(key, str)
        editor.commit()
    }

    /**
     * 프리퍼런스 값 가져오기
     */
    fun getPreference(key: String): String {
        val pref = getSharedPreferences(key, MODE_PRIVATE)
        return pref.getString(key, "")
    }

    /**
     * 앱 종료
     */
    fun exit() {
        finishAffinity()
        Handler().postDelayed({
            android.os.Process.killProcess(android.os.Process.myPid())
        }, 200)
    }

    override fun onDestroy() {
        super.onDestroy()
        DLogBaseApplication().setDeleteItem(ArrayList())
        DLogBaseApplication().setAllCheckBox(false)
//        val db = DBHelper(this)
//        db.close()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        DLogBaseApplication().setAllCheckBox(false)
        DLogBaseApplication().setDeleteItem(null)
    }

}

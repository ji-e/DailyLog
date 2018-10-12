package com.example.uohih.dailylog.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.adapter.DBData
import com.example.uohih.dailylog.adapter.WeeklyAdapter
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.view.TopTitleView
import kotlinx.android.synthetic.main.activity_weekly.*
import org.json.JSONObject


class WeeklyActivity : DLogBaseActivity() {

    private val base = DLogBaseApplication()
    private var jsonCalendar = JSONObject(getToday().toString())
    private val db = DBHelper(this)
    private val currentDate = getDate(false, 1, "주", jsonCalendar).get("yyyymmdd").toString()
    private var allCheck = base.getAllCheckBox()

    var dailyList = arrayListOf<DBData>()

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
            if (currentDate != nextCalendar.get("yyyymmdd").toString()) {
                DLogBaseApplication().setDateInfom(nextCalendar)
                weekly_tv_date.text = String.format(getString(R.string.weekly_date), nextCalendar.get("year"), nextCalendar.get("month"), nextCalendar.get("week"))
            }

//            setData(nextCalendar.get("yyyymmdd").toString())
        }

        // 연필 메뉴 안보이게
//        weekly_title_view.setGone(TopTitleView.menu.PENCIL)

        /**
         * 편집 클릭 리스너
         */
        weekly_title_view.setmClickListener(object : TopTitleView.mClickListener {
            override fun onmClickEvent() {
                allCheck = base.getAllCheckBox()
                if (allCheck) {
                    // 상단 바 지우개 클릭 이벤트
                    weekly_title_view.setEraserBtnClickListener(View.OnClickListener {
                        weekly_check.isChecked = false
                        val array = base.getDeleteItem()
                        db.delete(array, "no")
                        setData(base.getDateInfom(), allCheck)
                        Toast.makeText(mContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                    })
                    weekly_checkbox.visibility = View.VISIBLE
                    setData(base.getDateInfom(), allCheck)
                } else {
                    // 연필 메뉴 안보이게
                    weekly_title_view.setGone(TopTitleView.menu.PENCIL)
                    setData(base.getDateInfom(), allCheck)
                    weekly_checkbox.visibility = View.GONE
                }
            }
        })


    }

    override fun onResume() {
        LogUtil.d("onResume")
        super.onResume()

        // 날짜 데이터 세팅
//        if (create) {
        setData(jsonCalendar, false)
//            base.setDateInfom(jsonCalendar)
//        } else {
//            setData(base.getDateInfom(), allCheck)
//        }
//
//
//        create = false
    }

    fun selector(p: DBData): Int = p.date
    private fun setData(jsonObject: JSONObject, delete: Boolean) {
        // 날짜 파싱
        weekly_tv_date.text = String.format(getString(R.string.weekly_date), jsonCalendar.get("year"), jsonCalendar.get("month"), jsonCalendar.get("week"))
        DLogBaseApplication().setDateInfom(jsonCalendar)
        val dateList = weekCalendar(jsonObject.get("yyyymmdd").toString())
        val first = dateList[0].toInt()
        val last = dateList[6].toInt()
        val cursor = db.select(first, last)

        dailyList.clear()

        for (i in 0..6) {
            dailyList.add(DBData(null, dateList[i].toInt(), getString(R.string.weekly_noting), ""))
        }


        while (cursor.moveToNext()) {
            dailyList.add(DBData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
        }

        // 오름차순으로 정렬
        dailyList.sortBy({ selector(it) })


        /**
         * weeklyList 데이터 관리 (일정이 없는 것은 "일정을 작성해 주세요"
         */
        var tempDate = dateList[0].toInt()
        var weeklyList = arrayListOf<DBData>()
        var index = 1
        var i = 1
        do {
            val temp = dailyList[i].date
            if (tempDate == temp) {
                if (i == dailyList.size - 1) {
                    weeklyList.add(dailyList[i])
                    break
                }
                if (tempDate == dailyList[i + 1].date) {
                    weeklyList.add(dailyList[i + 1])
                    i++
                } else {
                    weeklyList.add(dailyList[i])
                }
                if (tempDate != dateList[6].toInt()) {
                    tempDate = dateList[index++].toInt()
                }
            } else if ((tempDate != temp) && (tempDate > temp)) {
                weeklyList.add(dailyList[i])
            } else {
                weeklyList.add(dailyList[i - 1])
                if (tempDate != dateList[6].toInt()) {
                    tempDate = dateList[index++].toInt()
                }
            }
            i++

        } while (i < dailyList.size)


        val mAadapter = WeeklyAdapter(this, weeklyList)
        weekly_listview.adapter = mAadapter


//
//        daily_recyclerView.adapter = mAadapter
//
//        val lm = LinearLayoutManager(this)
//        daily_recyclerView.layoutManager = lm
//        daily_recyclerView.setHasFixedSize(true)
//
//        /**
//         * 전체 선택 체크박스 클릭 이벤트
//         */
//        daily_check.setOnClickListener {
//            if (daily_check.isChecked) {
//                mAadapter.setAllCheckList(true)
//            } else {
//                mAadapter.setAllCheckList(false)
//            }
//        }
//
//        /**
//         * 전체 선택 해제 리스너
//         */
//        mAadapter.setmCheckboxListener(object : DailyRvAadapter.mCheckboxListener {
//            override fun onmClickEvent() {
//                daily_check.isChecked = false
//            }
//        })
    }
}


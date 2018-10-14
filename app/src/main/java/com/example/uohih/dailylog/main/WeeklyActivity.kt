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
    private var mAadapter: WeeklyAdapter? = null

    var dailyList = arrayListOf<DBData>()

    private var create = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly)

        // 상단 바 캘린더 클릭 이벤트
        weekly_title_view.setCalendarBtnClickListener(View.OnClickListener {
            base.setDateInfom(jsonCalendar)
            setData(jsonCalendar, allCheck)

        })

        // 이전 버튼 클릭 이벤트
        weekly_btn_back.setOnClickListener {
            var preCalendar = JSONObject(getDate(true, 1, "주").toString())
            DLogBaseApplication().setDateInfom(preCalendar)
            setData(preCalendar, allCheck)
        }

        // 다음 버튼 클릭 이벤트
        weekly_btn_next.setOnClickListener {
            var nextCalendar = JSONObject(getDate(false, 1, "주").toString())
            if (currentDate > nextCalendar.get("yyyymmdd").toString()) {
                DLogBaseApplication().setDateInfom(nextCalendar)
                setData(nextCalendar, allCheck)
            }

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
                        mAadapter?.check()

                        val array = base.getDeleteItem()
                        db.delete(array, "date")
                        setData(base.getDateInfom(), allCheck)
                        Toast.makeText(mContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                    })
                    weekly_checkbox.visibility = View.VISIBLE
                    setData(base.getDateInfom(), allCheck)
                } else {
                    // 연필 메뉴 안보이게
//                    weekly_title_view.setGone(TopTitleView.menu.PENCIL)
                    setData(base.getDateInfom(), allCheck)
                    weekly_checkbox.visibility = View.GONE
                }
            }
        })

    }

    override fun onResume() {
        LogUtil.d("onResume")
        super.onResume()


        /**
         * 프리퍼런스에 액티비티 상태 저장
         */
        setPreference(activitySetting, "weekly")


        // 날짜 데이터 세팅
//        if (create) {
//            setData(jsonCalendar, false)
//            base.setDateInfom(jsonCalendar)
//        } else {
        setData(base.getDateInfom(), allCheck)
//        }


        create = false
    }

    fun selector(p: DBData): Int = p.date
    private fun setData(jsonObject: JSONObject, delete: Boolean) {
        // 날짜 파싱
        weekly_tv_date.text = String.format(getString(R.string.weekly_date), jsonObject.get("year"), jsonObject.get("month"), jsonObject.get("week"))
        DLogBaseApplication().setDateInfom(jsonObject)
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
        dailyList.sortBy { selector(it) }


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
                if ((tempDate == dateList[6].toInt()) && (i == dailyList.size - 1)) {
                    weeklyList.add(dailyList[i])
                }
            }

            i++

        } while (i < dailyList.size)


        mAadapter = WeeklyAdapter(this, weeklyList, delete)
        weekly_listview.adapter = mAadapter

        /**
         * 전체 선택 해제 리스너
         */
        mAadapter?.setmCheckboxListener(object : WeeklyAdapter.mCheckboxListener {
            override fun onmClickEvent() {
                weekly_check.isChecked = false
            }
        })


        /**
         * 전체 선택 체크박스 클릭 이벤트
         */
        weekly_check.setOnClickListener {
            if (weekly_check.isChecked) {
                mAadapter?.setAllCheckList(true)
            } else {
                mAadapter?.setAllCheckList(false)
            }
        }
    }
}


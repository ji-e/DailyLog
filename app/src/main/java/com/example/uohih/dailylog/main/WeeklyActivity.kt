package com.example.uohih.dailylog.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.adapter.DBData
import com.example.uohih.dailylog.adapter.WeeklyAdapter
import com.example.uohih.dailylog.base.BackPressCloseHandler
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.view.TopTitleView
import kotlinx.android.synthetic.main.activity_weekly.*
import org.json.JSONObject

/**
 * 주간일지
 */
class WeeklyActivity : DLogBaseActivity() {

    private val base = DLogBaseApplication()
    private var jsonCalendar = JSONObject(getToday().toString())
    private val db = DBHelper(this)
    private val currentDate = getDate(false, 1, "주", jsonCalendar).get("yyyymmdd").toString()
    private var allCheck = base.getAllCheckBox()
    private var mAadapter: WeeklyAdapter? = null

    var weeklyList = arrayListOf<DBData>()

    private var create = false
    private var noBack = false

    // back key exit
    private lateinit var backPressCloseHandler: BackPressCloseHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly)

        // back key exit 초기화
        backPressCloseHandler = BackPressCloseHandler(this)

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

        /**
         * 편집 클릭 리스너
         */
        weekly_title_view.setmClickListener(object : TopTitleView.mClickListener {
            override fun onmClickEvent() {
                noBack = true
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

        setData(base.getDateInfom(), allCheck)

        create = false
    }

    fun selector(p: DBData): Int = p.date
    /**
     * DB에서 데이터 가져와 set
     * jsonObject: JSONObject
     * delete: Boolean (true: 체크박스 활성화)
     */
    private fun setData(jsonObject: JSONObject, delete: Boolean) {
        // 날짜 파싱
        weekly_tv_date.text = String.format(getString(R.string.weekly_date), jsonObject.get("year"), jsonObject.get("month"), jsonObject.get("week"))
        DLogBaseApplication().setDateInfom(jsonObject)
        val dateList = weekCalendar(jsonObject.get("yyyymmdd").toString())
        val first = dateList[0].toInt()
        val last = dateList[6].toInt()
        val cursor = db.select(first, last)

        weeklyList.clear()

        for (i in 0..6) {
            weeklyList.add(DBData(null, dateList[i].toInt(), getString(R.string.weekly_noting), ""))
        }


        while (cursor.moveToNext()) {
            weeklyList.add(DBData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
        }

        // 오름차순으로 정렬
        weeklyList.sortBy { selector(it) }



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

    /**
     * Back Key
     */
    override fun onBackPressed() {
//        super.onBackPressed()
        if (noBack) {
            weekly_title_view.setLogo(getString(R.string.daily_title))
        } else {
            backPressCloseHandler.onBackPressed()
        }
    }
}


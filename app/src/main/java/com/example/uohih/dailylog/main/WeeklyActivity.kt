package com.example.uohih.dailylog.main

import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.database.DBHelper
import kotlinx.android.synthetic.main.activity_weekly.*
import org.json.JSONObject
import java.util.*
import java.util.Collections.sort


class WeeklyActivity : DLogBaseActivity() {

    private val tag = "WeeklyActivity"
    private var jsonCalendar = JSONObject(getToday().toString())
    private val db = DBHelper(this)
    private val currentDate = getDate(false, 1, "주", jsonCalendar).get("yyyymmdd").toString()

    var dailyList = arrayListOf<DailyData>()

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

    fun selector(p: DailyData):Int = p.date
    private fun setData(jsonObject: JSONObject, delete: Boolean) {
        // 날짜 파싱
        weekly_tv_date.text = String.format(getString(R.string.weekly_date), jsonCalendar.get("year"), jsonCalendar.get("month"), jsonCalendar.get("week"))
        DLogBaseApplication().setDateInfom(jsonCalendar)
        val dateList = weekCalendar(jsonObject.get("yyyymmdd").toString())
        val first = dateList[0].toInt()
        val last = dateList[6].toInt()
        val cursor = db.select(first, last)

        dailyList.clear()

        for(i in 0..6){
            dailyList.add(DailyData(null, dateList[i].toInt(), getString(R.string.weekly_noting), ""))
        }


        while (cursor.moveToNext()) {
            dailyList.add(DailyData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
        }

        dailyList.sortBy({selector(it)})

        var tempDate = dateList[0].toInt()
        var weeklyList = arrayListOf<DailyData>()
        var index = 1
        var i = 1
        do {
            val temp = dailyList[i].date
            if (tempDate == temp) {
                if(i==dailyList.size-1){
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


        val mAadapter = WeekllyListAdapter(this, weeklyList, delete)
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

class WeekllyListAdapter(val mContext: Context, val dailyList: ArrayList<DailyData>, val delete: Boolean) : BaseAdapter() {

    /* 아이템을 세트로 담기 위한 어레이 */
//    private val mItems = ArrayList()

    override fun getCount(): Int {
        return dailyList.size
    }

    override fun getItem(position: Int): DailyData {
        return dailyList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var preItem: Int? = null
        if (position > 0) {
            preItem = getItem(position - 1).date
        }

        lateinit var viewHolder: ViewHolder
        var view = convertView
        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.weekly_item_listview, parent, false)
            viewHolder.itemTitle = view.findViewById(R.id.weekly_item_tv)
            viewHolder.itemDate = view.findViewById(R.id.weekly_item_date)
            viewHolder.itemImg = view.findViewById(R.id.weekly_item_img)
            view.tag = viewHolder
            viewHolder.itemTitle.text = dailyList[position].title

            val date = dailyList[position].date.toString()
            viewHolder.itemDate.text = String.format(view.resources.getString(R.string.weekly_sub_date), date.substring(4, 6), date.substring(6), DLogBaseActivity().getDay(date))

            if (viewHolder.itemTitle == null) {
                viewHolder.itemImg.visibility = View.GONE
            }

            // 첫번째 리스트 항목 거래일 뷰 표시
            if (position == 0) {
                viewHolder.itemDate.visibility = View.VISIBLE
            } else {
                // 거래일이 같으면 거래일 뷰 숨김
                if (preItem == dailyList[position].date) {
                    viewHolder.itemDate.visibility = View.GONE
                    // 거래일이 다르면 거래일 뷰 표시
                } else {
                    viewHolder.itemDate.visibility = View.VISIBLE
                }
            }

            return view
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.itemTitle.text = dailyList[position].title
        return view
    }

    inner class ViewHolder {
        lateinit var itemTitle: TextView
        lateinit var itemDate: TextView
        lateinit var itemImg: ImageView

    }

}
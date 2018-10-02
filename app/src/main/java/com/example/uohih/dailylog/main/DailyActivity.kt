package com.example.uohih.dailylog.main

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.view.TopTitleView
import kotlinx.android.synthetic.main.activity_daily.*
import kotlinx.android.synthetic.main.dialog_menu.*
import kotlinx.android.synthetic.main.dialog_menu.view.*
import org.json.JSONObject

class DailyActivity : DLogBaseActivity() {
    private val tag = "DailyActivity"
    private var jsonCalendar = JSONObject(getToday().toString())
    private val db = DBHelper(this)
    private val currentDate = jsonCalendar.get("yyyymmdd").toString()


    // 리사이클 뷰
    var dailyList = arrayListOf<DailyData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)

        // 상단 바 캘린더 클릭 이벤트
        daily_title_view.setCalendarBtnClickListener(View.OnClickListener {
            Toast.makeText(mContext, jsonCalendar.get("yyyymmdd").toString(), Toast.LENGTH_SHORT).show()
            setData(jsonCalendar)

        })

        // 이전 버튼 클릭 이벤트
        daily_btn_back.setOnClickListener {
            var preCalendar = JSONObject(getDate(true,1,"일").toString())
            setDateInfom(preCalendar)
            setData(preCalendar)

        }

        // 다음 버튼 클릭 이벤트
        daily_btn_next.setOnClickListener {
            var nextCalendar = JSONObject(getDate(false,1,"일").toString())
            setDateInfom(nextCalendar)
            setData(nextCalendar)

        }


    }


    override fun onResume() {
        Log.d(tag, "onResume")
        super.onResume()

        // 날짜 데이터 세팅
        setData(jsonCalendar)


    }

    private fun setData(jsonObject: JSONObject) {
        // 날짜 파싱
        daily_tv_date.text = String.format(getString(R.string.daily_date), jsonObject.get("year"), jsonObject.get("month"), jsonObject.get("date"), jsonObject.get("day"))

        val cursor = db.select(jsonObject.get("yyyymmdd").toString().toInt())
        dailyList.clear()
        while (cursor.moveToNext()) {
            dailyList.add(DailyData(cursor.getString(2), cursor.getString(3)))
        }

        val mAadapter = DailyRvAadapter(this, dailyList)

        daily_recyclerView.adapter = mAadapter

        val lm = LinearLayoutManager(this)
        daily_recyclerView.layoutManager = lm
        daily_recyclerView.setHasFixedSize(true)
    }

}

class DailyData(val title: String, val content: String)

/**
 * daily
 * 리사이클뷰 아답터
 */
class DailyRvAadapter(val mContext: Context, val dailyList: ArrayList<DailyData>) : RecyclerView.Adapter<DailyRvAadapter.Holder>() {
    /**
     * 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성한다.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.daily_item_recycler_view, parent, false)
        return Holder(view)
    }

    /**
     * RecyclerView로 만들어지는 item의 총 개수를 반환한다.
     */
    override fun getItemCount(): Int {
        return dailyList.size
    }

    /**
     * 위의 onCreateViewHolder에서 만든 view와 실제 입력되는 각각의 데이터를 연결한다.
     */
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dailyList[position], mContext)
    }


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle = itemView.findViewById<TextView>(R.id.tv_daily_item_title)
        val itemContent = itemView.findViewById<TextView>(R.id.tv_daily_item_content)
        val itemBtn = itemView.findViewById<ImageButton>(R.id.btn_daily_item)
        val itemSrc = itemView.findViewById<ScrollView>(R.id.scr_daily_item)

        var down = true


        fun bind(data: DailyData, context: Context) {
            // 나머지 TextView와 String 데이터를 연결
            itemTitle?.text = data.title
            itemContent?.text = data.content

            // 화살표 클릭 이벤트
            itemBtn.setOnClickListener {
                if (down) {
                    itemSrc.visibility = View.VISIBLE
                    itemBtn.setBackgroundResource(R.drawable.btn_up_selector)
                    down = false
                } else {
                    itemSrc.visibility = View.GONE
                    itemBtn.setBackgroundResource(R.drawable.btn_down_selector)
                    down = true
                }
            }


        }


    }
}
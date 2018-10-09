package com.example.uohih.dailylog.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.view.CustomListDialog
import com.example.uohih.dailylog.view.ListViewAdapter
import com.example.uohih.dailylog.view.TopTitleView
import kotlinx.android.synthetic.main.activity_daily.*
import org.json.JSONObject


class DailyActivity : DLogBaseActivity() {
    private val base = DLogBaseApplication()
    private var jsonCalendar = JSONObject(getToday().toString())
    private val db = DBHelper(this)
    private val currentDate = getDate(false, 1, "일", jsonCalendar).get("yyyymmdd").toString()
    private var allCheck = base.getAllCheckBox()


    // 리사이클 뷰
    var dailyList = arrayListOf<DailyData>()

    private var create = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)
        LogUtil.d("onCreate()")
        create = true

        // 상단 바 캘린더 클릭 이벤트
        daily_title_view.setCalendarBtnClickListener(View.OnClickListener {
            base.setDateInfom(jsonCalendar)
            setData(jsonCalendar, allCheck)

        })


        // 이전 버튼 클릭 이벤트
        daily_btn_back.setOnClickListener {
            var preCalendar = JSONObject(getDate(true, 1, "일").toString())
            base.setDateInfom(preCalendar)
            setData(preCalendar, allCheck)

        }

        // 다음 버튼 클릭 이벤트
        daily_btn_next.setOnClickListener {
            var nextCalendar = JSONObject(getDate(false, 1, "일").toString())
            if (currentDate != nextCalendar.get("yyyymmdd").toString()) {
                base.setDateInfom(nextCalendar)
                setData(nextCalendar, allCheck)
            }
        }


        daily_title_view.setmClickListener(object : TopTitleView.mClickListener {
            override fun onmClickEvent() {
                allCheck = base.getAllCheckBox()
                if (allCheck) {
                    // 상단 바 지우개 클릭 이벤트
                    daily_title_view.setEraserBtnClickListener(View.OnClickListener {
                        daily_check.isChecked = false
                        val array = base.getDeleteItem()
                        db.delete(array, "no")
                        setData(base.getDateInfom(), allCheck)
                        Toast.makeText(mContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                    })
                    daily_checkbox.visibility = View.VISIBLE
                    setData(base.getDateInfom(), allCheck)
                } else {
                    setData(base.getDateInfom(), allCheck)
                    daily_checkbox.visibility = View.GONE
                }
            }
        })


    }


    override fun onResume() {
        LogUtil.d("onResume")
        super.onResume()

        // 날짜 데이터 세팅
        if (create) {
            setData(jsonCalendar, allCheck)
            base.setDateInfom(jsonCalendar)
        } else {
            setData(base.getDateInfom(), allCheck)
        }


        create = false
    }

    private fun setData(jsonObject: JSONObject, delete: Boolean) {
        // 날짜 파싱
        daily_tv_date.text = String.format(getString(R.string.daily_date), jsonObject.get("year"), jsonObject.get("month"), jsonObject.get("date"), jsonObject.get("day"))
        val cursor = db.select(jsonObject.get("yyyymmdd").toString().toInt())
        dailyList.clear()
        while (cursor.moveToNext()) {
            dailyList.add(DailyData(cursor.getInt(0),cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
        }

        val mAadapter = DailyRvAadapter(this, dailyList, delete)

        daily_recyclerView.adapter = mAadapter

        val lm = LinearLayoutManager(this)
        daily_recyclerView.layoutManager = lm
        daily_recyclerView.setHasFixedSize(true)

        /**
         * 전체 선택 체크박스 클릭 이벤트
         */
        daily_check.setOnClickListener {
            if (daily_check.isChecked) {
                mAadapter.setAllCheckList(true)
            } else {
                mAadapter.setAllCheckList(false)
            }
        }

        /**
         * 전체 선택 해제 리스너
         */
        mAadapter.setmCheckboxListener(object : DailyRvAadapter.mCheckboxListener {
            override fun onmClickEvent() {
                daily_check.isChecked = false
            }
        })
    }
}

class DailyData(val no: Int, val date:Int, val title: String, val content: String)

/**
 * daily
 * 리사이클뷰 아답터
 */
class DailyRvAadapter(val mContext: Context, val dailyList: ArrayList<DailyData>, val delete: Boolean) : RecyclerView.Adapter<DailyRvAadapter.Holder>() {

    private val base = DLogBaseApplication()
    private var isSelectedAll = false // 전체 선택 프래그
    var mListener: mCheckboxListener? = null // 전체 선택 해제 리스너

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
        holder.itemCheck.isChecked = isSelectedAll

    }

    /**
     * 전체 선택 및 해제
     */
    fun setAllCheckList(isCheck: Boolean) {
        isSelectedAll = isCheck
        notifyDataSetChanged()
    }

    interface mCheckboxListener {
        fun onmClickEvent()
    }

    fun setmCheckboxListener(listener: mCheckboxListener) {
        this.mListener = listener
    }

    fun removeAt(position: Int) {
        dailyList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dailyList.size)
    }


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle = itemView.findViewById<TextView>(R.id.tv_daily_item_title)
        val itemContent = itemView.findViewById<TextView>(R.id.tv_daily_item_content)
        val itemBtn = itemView.findViewById<ImageButton>(R.id.btn_daily_item)
        val itemCheck = itemView.findViewById<CheckBox>(R.id.check_daily_item)
        val itemSrcLin = itemView.findViewById<LinearLayout>(R.id.scr_lin_daily_item)
        val itemSrc = itemView.findViewById<ScrollView>(R.id.scr_daily_item)
        val layout = itemView.findViewById<LinearLayout>(R.id.lin_daily_item)
        var down = true


        @SuppressLint("ClickableViewAccessibility")
        fun bind(data: DailyData, context: Context) {
            // 나머지 TextView와 String 데이터를 연결
            itemTitle.text = data.title
            itemContent?.text = data.content
            var no = data.no


            if (delete) {
                itemBtn.visibility = View.GONE
                itemCheck.visibility = View.VISIBLE
            } else {
                itemCheck.visibility = View.GONE
                itemBtn.visibility = View.VISIBLE
            }


            /**
             * 상세 내용이 없을 때
             */
            if (data.content.isBlank()) {
                itemBtn.visibility = View.GONE
            }


            /**
             * 텍스트뷰 터치 이벤트 죽이기
             */
            itemContent.setOnTouchListener { v, event ->
                // TODO Auto-generated method stub
                itemSrc.requestDisallowInterceptTouchEvent(true)
                //스크롤뷰가 텍스트뷰의 터치이벤트를 가져가지 못하게 함
                false
            }

            /**
             * 레이아웃 클릭 이벤트
             */
            layout.setOnClickListener {
                if (itemCheck.visibility == View.VISIBLE) {
                    if (itemCheck.isChecked) {
                        itemCheck.isChecked = false
                    } else {
                        // 전체 선택 해제 리스너
                        if (mListener != null) {
                            mListener?.onmClickEvent()
                        }
                        itemCheck.isChecked = true
                    }

                } else if (itemBtn.visibility == View.VISIBLE) {
                    if (down) {
                        itemSrcLin.visibility = View.VISIBLE
                        itemBtn.setBackgroundResource(R.drawable.btn_up_selector)
                        down = false
                    } else {
                        itemSrcLin.visibility = View.GONE
                        itemBtn.setBackgroundResource(R.drawable.btn_down_selector)
                        down = true
                    }
                }
            }

            /**
             * 레이아웃 롱클릭 이벤트
             */
            layout.setOnLongClickListener {
                val listViewAdapter = ListViewAdapter((mContext as Activity), ArrayList())
                listViewAdapter.setContent("수정")
                listViewAdapter.setContent("삭제")


                var customDialogList = CustomListDialog(mContext, android.R.style.Theme_Material_Dialog_MinWidth)
                customDialogList = customDialogList.showDialogList(mContext, itemTitle.text.toString(), DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int -> }, listViewAdapter, AdapterView.OnItemClickListener { parent, view, position, id ->
                    when (position) {
                        0 -> { //수정

                            var jsonObject=JSONObject()
                            jsonObject.put("no",data.no)
                            jsonObject.put("date",data.date)
                            jsonObject.put("title",data.title)
                            jsonObject.put("content",data.content)

                            val intent = Intent(mContext, UpdateActivity::class.java)
                            intent.putExtra("daily",jsonObject.toString())
                            mContext.startActivity(intent)
                            Toast.makeText(mContext, "수정", Toast.LENGTH_SHORT).show()
                        }
                        1 -> { //삭제
                            DBHelper(mContext).delete(arrayListOf(no.toString()), "no")
                            removeAt(adapterPosition)
                            Toast.makeText(mContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                        }

                    }
//                    postDelayed({
                    customDialogList.dismiss()
//                    }, 500)

                })!!
                customDialogList.show()
                return@setOnLongClickListener true
            }

            /**
             * 화살표 클릭 이벤트
             */
            itemBtn.setOnClickListener {
                if (down) {
                    itemSrcLin.visibility = View.VISIBLE
                    itemBtn.setBackgroundResource(R.drawable.btn_up_selector)
                    down = false
                } else {
                    itemSrcLin.visibility = View.GONE
                    itemBtn.setBackgroundResource(R.drawable.btn_down_selector)
                    down = true
                }
            }


            /**
             * 체크 박스 클릭 이벤트
             */
            itemCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                var array = ArrayList<String>()
                if (isChecked) {
                    base.setCheckOnItem(position.toString(), no.toString())
                } else {
                    // 전체 선택 해제 리스너
                    if (mListener != null) {
                        mListener?.onmClickEvent()
                    }
                    base.setCheckOffItem(position.toString())
                }
                var int = 0
                for (i in 0..itemCount) {
                    if (!DLogBaseApplication().getCheckItem().optString(i.toString()).isNullOrEmpty()) {
                        array.add(int, base.getCheckItem().get(i.toString()).toString())
                        int++
                    }
                }
                base.setDeleteItem(array)
            }
        }
    }

}

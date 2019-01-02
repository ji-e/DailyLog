package com.example.uohih.dailylog.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.main.DailyActivity
import com.example.uohih.dailylog.main.UpdateActivity
import com.example.uohih.dailylog.view.CustomListDialog
import org.json.JSONObject
import java.util.*

/**
 * 주간 일지 아답터
 * mContext: Context
 * weeklyList: ArrayList<DBData>: 주간 일지 정보 리스트
 * delete: Boolean  (삭제: true)
 */
class WeeklyAdapter(private val mContext: Context, private val weeklyList: ArrayList<DBData>, private var delete: Boolean) : BaseAdapter() {
    // 체크 된 항목
    private var selected = ArrayList<Boolean>(count)

    /**
     * ----------- 전체 선택 체크박스 리스너 start -----------
     */
    private var mListener: mCheckboxListener? = null

    interface mCheckboxListener {
        fun onmClickEvent()
    }

    fun setmCheckboxListener(listener: mCheckboxListener) {
        this.mListener = listener
    }
    /**
     * ----------- 전체 선택 체크박스 리스너 end -----------
     */

    /**
     * 전체 선택 및 해제
     * isCheck: Boolean: 체크 상태 값
     */
    fun setAllCheckList(isCheck: Boolean) {
        selected.clear()
        for (i in 0 until count) {
            selected.add(isCheck)
        }
        notifyDataSetChanged()
    }

    /**
     * 체크 된 항목
     */
    fun check() {
        var array = ArrayList<String>()
        for (i in 0 until count) {
            if (selected[i]) {
                array.add(weeklyList[i].date.toString())
            }
        }
        notifyDataSetChanged()
        DLogBaseApplication().setDeleteItem(array)
    }


    /**
     * 데이터 삭제
     * position: Int: 삭제할 index
     */
    fun removeAt(position: Int) {
        weeklyList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return weeklyList.size
    }

    override fun getItem(position: Int): DBData {
        return weeklyList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var preItem: Int? = null
        var nextItem: Int? = null
        if (position < count - 1)
            nextItem = getItem(position + 1).date
        if (position > 0) {
            preItem = getItem(position - 1).date
        }

        lateinit var holder: ViewHolder
        var view = convertView
        if (view == null) {
            holder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.weekly_item_listview, parent, false)
            holder.itemTitle = view.findViewById(R.id.weekly_item_tv)
            holder.itemDate = view.findViewById(R.id.weekly_item_date_tv)
            holder.itemImg = view.findViewById(R.id.weekly_item_img)
            holder.itemLinear = view.findViewById(R.id.weekly_item_date)
            holder.itemTitleLin = view.findViewById(R.id.weekly_item_lin)
            holder.itemCheck = view.findViewById(R.id.weekly_item_check)
            view.tag = holder

        } else {
            holder = view.tag as ViewHolder
        }

        // 체크 초기값 세팅 (false)
        for (i in 0 until count) {
            selected.add(false)
        }

        // delete: true: 체크박스, false: V버튼
        if (delete) {
            holder.itemImg.visibility = View.GONE
            holder.itemCheck.visibility = View.VISIBLE
        } else {
            holder.itemCheck.visibility = View.GONE
            holder.itemImg.visibility = View.VISIBLE
        }

        // 날짜 텍스트 설정
        if (weeklyList[position] != null) {
            val date = weeklyList[position].date.toString()
            holder.itemDate.text = String.format(view?.resources?.getString(R.string.weekly_sub_date).toString(), date.substring(4, 6), date.substring(6), DLogBaseActivity().getDay(date))
            holder.itemTitle.text = weeklyList[position].title
        }



        if (position == 0) {
            // 첫번째 리스트 항목 거래일 뷰 표시
            holder.itemLinear.visibility = View.VISIBLE
        } else {
            if (preItem == weeklyList[position].date) {
                // 거래일이 같으면 거래일 뷰 숨김
                holder.itemLinear.visibility = View.GONE
            } else {
                // 거래일이 다르면 거래일 뷰 표시
                holder.itemLinear.visibility = View.VISIBLE
            }
        }

        // 일지를 작성해주세요. 텍스트 설정
        if (nextItem == weeklyList[position].date) {
            if (weeklyList[position].no == null) {
                holder.itemTitleLin.visibility = View.GONE
            }
        } else {
            holder.itemTitleLin.visibility = View.VISIBLE
        }
        if (view != null) {
            if (holder.itemTitle.text == view?.resources?.getString(R.string.weekly_noting))
                holder.itemTitle.setTextColor(view.resources.getColor(R.color.c_777777))
        }

        // 날짜에 따른 일지 타이틀 설정
        holder.itemTitle.text = weeklyList[position].title


        /**
         * 날짜 클릭 이벤트
         */
        if (weeklyList[position].date <= DLogBaseActivity().getToday().get("yyyymmdd").toString().toInt()) {
            holder.itemLinear.setOnClickListener {
                if (holder.itemCheck.visibility == View.VISIBLE) {
                    if (holder.itemCheck.isChecked) {
                        holder.itemCheck.isChecked = false
                    } else {
                        // 전체 선택 해제 리스너
                        if (mListener != null) {
                            mListener?.onmClickEvent()
                        }
                        holder.itemCheck.isChecked = true
                    }
                } else if (holder.itemImg.visibility == View.VISIBLE) {
                    val intent = Intent(mContext, DailyActivity::class.java)
                    intent.putExtra("weekly", weeklyList[position].date.toString())
                    mContext.startActivity(intent)
                }
            }
        }


        /**
         * 타이틀 롱클릭 이벤트
         */
        if (holder.itemTitle.text != view?.resources?.getString(R.string.weekly_noting)) {
            holder.itemTitleLin.setOnLongClickListener {
                val listViewAdapter = DialogAdapter((mContext as Activity), ArrayList())
                listViewAdapter.setContent(it.resources.getString(R.string.menu_05))
                listViewAdapter.setContent(it.resources.getString(R.string.menu_06))

                var customDialogList = CustomListDialog(mContext, android.R.style.Theme_Material_Dialog_MinWidth)
                customDialogList = customDialogList.showDialogList(mContext, holder.itemTitle.text.toString(), DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int -> }, listViewAdapter, AdapterView.OnItemClickListener { parent, view, p, id ->
                    when (p) {
                        0 -> { //수정
                            var jsonObject = JSONObject()
                            jsonObject.put("no", weeklyList[position].no)
                            jsonObject.put("date", weeklyList[position].date)
                            jsonObject.put("title", weeklyList[position].title)
                            jsonObject.put("content", weeklyList[position].content)

                            val intent = Intent(mContext, UpdateActivity::class.java)
                            intent.putExtra("daily", jsonObject.toString())
                            mContext.startActivity(intent)
                            Toast.makeText(mContext, "수정", Toast.LENGTH_SHORT).show()
                        }
                        1 -> { //삭제
                            DBHelper(mContext).delete(arrayListOf(weeklyList[position].no.toString()), "no")
                            removeAt(position)
                            Toast.makeText(mContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                        }

                    }
                    customDialogList.dismiss()
                })!!
                customDialogList.show()

                return@setOnLongClickListener true
            }
        }

        /**
         * 체크 박스 클릭 이벤트
         */
        if (holder.itemCheck.visibility == View.VISIBLE) {
            holder.itemCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                // 체크 상태값 변환
                selected[position] = isChecked
                // 전체 선택 해제 리스너
                if (!isChecked && mListener != null) {
                    mListener?.onmClickEvent()
                }
            }
        }

        // 체크 상태 설정
        if (selected.isNotEmpty()) {
            holder.itemCheck.isChecked = selected[position]
        } else {
            holder.itemCheck.isChecked = false
        }

        return view!!
    }

    inner class ViewHolder {
        lateinit var itemTitle: TextView
        lateinit var itemDate: TextView
        lateinit var itemImg: ImageView
        lateinit var itemLinear: LinearLayout
        lateinit var itemTitleLin: RelativeLayout
        lateinit var itemCheck: CheckBox
    }

}
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
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.main.DailyActivity
import com.example.uohih.dailylog.main.UpdateActivity
import com.example.uohih.dailylog.view.CustomListDialog
import com.example.uohih.dailylog.view.ListViewAdapter
import org.json.JSONObject
import java.util.ArrayList

class WeeklyAdapter(private val mContext: Context, private val weeklyList: ArrayList<DBData>, private var delete: Boolean) : BaseAdapter() {
    private var selected = ArrayList<Boolean>(count) // 체크 된 항목

    /**
     * 전체 선택 체크박스 리스너
     */
    private var mListener: mCheckboxListener? = null

    interface mCheckboxListener {
        fun onmClickEvent()
    }

    fun setmCheckboxListener(listener: mCheckboxListener) {
        this.mListener = listener
    }

    /**
     * 전체 선택 및 해제
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


        for (i in 0 until count) {
            selected.add(false)
        }


        if (delete) {
            holder.itemImg.visibility = View.GONE
            holder.itemCheck.visibility = View.VISIBLE
        } else {
            holder.itemCheck.visibility = View.GONE
            holder.itemImg.visibility = View.VISIBLE
        }

        if (weeklyList[position] != null) {
            val date = weeklyList[position].date.toString()
            holder.itemDate.text = String.format(view?.resources?.getString(R.string.weekly_sub_date).toString(), date.substring(4, 6), date.substring(6), DLogBaseActivity().getDay(date))
            holder.itemTitle.text = weeklyList[position].title
        }

        if (view != null) {
            if (holder.itemTitle.text == view.resources.getString(R.string.weekly_noting)) {
                holder.itemTitle.setTextColor(view.resources.getColor(R.color.c_777777))
            }
        }

        // 첫번째 리스트 항목 거래일 뷰 표시
        if (position == 0) {
            holder.itemLinear.visibility = View.VISIBLE
        } else {
            // 거래일이 같으면 거래일 뷰 숨김
            if (preItem == weeklyList[position].date) {
                holder.itemLinear.visibility = View.GONE
                // 거래일이 다르면 거래일 뷰 표시
            } else {
                holder.itemLinear.visibility = View.VISIBLE
            }
        }

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
//                    (mContext as Activity).finish()
                }
            }
        }


        /**
         * 타이틀 롱클릭 이벤트
         */

        if (holder.itemTitle.text != view?.resources?.getString(R.string.weekly_noting)) {
            holder.itemTitleLin.setOnLongClickListener {
                val listViewAdapter = ListViewAdapter((mContext as Activity), ArrayList())
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
//                notifyDataSetChanged()
            }
        }


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
        lateinit var itemTitleLin: LinearLayout
        lateinit var itemCheck: CheckBox
    }

}
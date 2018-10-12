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
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.main.UpdateActivity
import com.example.uohih.dailylog.view.CustomListDialog
import com.example.uohih.dailylog.view.ListViewAdapter
import org.json.JSONObject
import java.util.ArrayList

class WeeklyAdapter(val mContext: Context, private val weeklyList: ArrayList<DBData>) : BaseAdapter() {


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
//            holder.itemImg = view.findViewById(R.id.weekly_item_img)
            holder.itemLinear = view.findViewById(R.id.weekly_item_date)
            holder.itemTitleLin = view.findViewById(R.id.weekly_item_lin)
            view.tag = holder


        } else {
            holder = view.tag as ViewHolder
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
         * 레이아웃 롱클릭 이벤트
         */
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


        return view!!
    }

    inner class ViewHolder {
        lateinit var itemTitle: TextView
        lateinit var itemDate: TextView
        lateinit var itemImg: ImageView
        lateinit var itemLinear: LinearLayout
        lateinit var itemTitleLin: LinearLayout
    }

}
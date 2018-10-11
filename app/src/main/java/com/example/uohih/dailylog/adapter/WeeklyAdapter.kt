package com.example.uohih.dailylog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import java.util.ArrayList

class WeeklyAdapter (val mContext: Context, val weeklyList: ArrayList<DBData>, val delete: Boolean) : BaseAdapter() {

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
            holder.itemDate = view.findViewById(R.id.weekly_item_date)
            holder.itemImg = view.findViewById(R.id.weekly_item_img)
            view.tag = holder


        } else {
            holder = view.tag as ViewHolder
        }

        if(weeklyList[position]!=null) {
            val date = weeklyList[position].date.toString()
            holder.itemDate.text = String.format(view?.resources?.getString(R.string.weekly_sub_date).toString(), date.substring(4, 6), date.substring(6), DLogBaseActivity().getDay(date))
            holder.itemTitle.text = weeklyList[position].title
        }

        // 첫번째 리스트 항목 거래일 뷰 표시
        if (position == 0) {
            holder.itemDate.visibility = View.VISIBLE
        } else {
            // 거래일이 같으면 거래일 뷰 숨김
            if (preItem == weeklyList[position].date) {
                holder.itemDate.visibility = View.GONE
                // 거래일이 다르면 거래일 뷰 표시
            } else {
                holder.itemDate.visibility = View.VISIBLE
            }
        }

        holder.itemTitle.text = weeklyList[position].title
        return view!!
    }

    inner class ViewHolder {
        lateinit var itemTitle: TextView
        lateinit var itemDate: TextView
        lateinit var itemImg: ImageView

    }

}
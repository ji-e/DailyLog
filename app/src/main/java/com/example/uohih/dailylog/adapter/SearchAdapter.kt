package com.example.uohih.dailylog.adapter

import android.annotation.SuppressLint
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
import com.example.uohih.dailylog.main.UpdateActivity
import com.example.uohih.dailylog.setting.SearchActivity
import com.example.uohih.dailylog.view.CustomListDialog
import com.example.uohih.dailylog.view.ListViewAdapter
import org.json.JSONObject

class SearchAdapter(private val mContext: Context, private val searchList: ArrayList<DBData>, private val search:Boolean) : BaseAdapter() {

    private val base = DLogBaseApplication()

    override fun getItem(position: Int): DBData {
        return searchList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return searchList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var holder: ViewHolder
        var view = convertView
        if (view == null) {
            holder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.search_item_listview, parent, false)
            holder.itemTitle = view.findViewById<TextView>(R.id.tv_search_item_title)
            holder.itemDate = view.findViewById<TextView>(R.id.tv_search_item_date)
            holder.layout = view.findViewById<LinearLayout>(R.id.lin_search_item)

            view.tag = holder

        } else {
            holder = view.tag as ViewHolder
        }

        holder.itemTitle.text = searchList[position].title
        holder.itemDate.text = (searchList[position].date).toString()

        /**
         * 레이아웃 클릭 이벤트
         */
        holder.layout.setOnClickListener {
            var jsonObject = JSONObject()
            jsonObject.put("no", searchList[position].no)
            jsonObject.put("date", searchList[position].date)
            jsonObject.put("title", searchList[position].title)
            jsonObject.put("content", searchList[position].content)

            base.setDateInfom(DLogBaseActivity().getToday(searchList[position].date.toString()))
            val intent = Intent(mContext, UpdateActivity::class.java)
            intent.putExtra("daily", jsonObject.toString())
            (mContext as Activity).startActivity(intent)
        }

        if(!search){
            holder.itemDate.visibility=View.GONE
            holder.itemTitle.textSize= 15F
        }
        return view!!
    }

    inner class ViewHolder {
        lateinit var itemTitle: TextView
        lateinit var itemDate: TextView
        lateinit var layout: LinearLayout
    }


}


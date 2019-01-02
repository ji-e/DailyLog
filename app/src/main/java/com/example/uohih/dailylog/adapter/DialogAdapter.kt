package com.example.uohih.dailylog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.uohih.dailylog.R

/**
 * 리스트 다이얼로그 아답터
 * context: Context
 * item: ArrayList<String>: 리스트 텍스트 정보
 */
class DialogAdapter(mContext: Context, item: ArrayList<String>) : BaseAdapter() {
    private val mContext = mContext
    private val mItem = item

    /**
     * 리스트에 값 추가
     * text: String: 추가할 텍스트
     */
    fun setContent(text: String) {
        mItem.add(text)
    }

    override fun getItem(position: Int) = mItem[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mItem.size

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        lateinit var viewHolder: ViewHolder
        var convertView = view

        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_list_item, parent, false)
            viewHolder.textView = convertView.findViewById(R.id.dailog_list_item)

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        // 리스트 값 텍스트 설정
        viewHolder.textView.text = mItem[position]

        convertView?.tag = viewHolder
        return convertView!!
    }

    inner class ViewHolder {
        lateinit var textView: TextView
    }

}
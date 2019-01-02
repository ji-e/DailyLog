package com.example.uohih.dailylog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import com.example.uohih.dailylog.R

/**
 * 숫자 키패드 아답터
 * mContext: Context
 * keyPadData: ArrayList<String>: 숫자 정보 리스트
 */
class KeyPadAdapter(private val mContext: Context, private val keyPadData: ArrayList<String>) : BaseAdapter() {


    /**
     * ----------- 버튼클릭 리스너 start -----------
     */
    private var mListener: mKeyPadListener? = null

    interface mKeyPadListener {
        fun onNumClickEvent(index: String)
        fun onEraserClickEvent()
    }

    fun setmKeyPadListener(listener: mKeyPadListener) {
        this.mListener = listener
    }
    /**
     * ----------- 버튼클릭 리스너 end -----------
     */

    override fun getCount(): Int {
        return keyPadData.size
    }

    override fun getItem(position: Int): Any {
        return keyPadData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var holder: ViewHolder
        var view = convertView
        if (view == null) {
            holder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.key_pad_item, parent, false)
            holder.itemBtn = view.findViewById(R.id.keypad_btn)
            holder.itemimg01 = view.findViewById(R.id.keypad_img01)
            holder.itemimg02 = view.findViewById(R.id.keypad_img02)
            holder.itemback=view.findViewById(R.id.keypad_back)

            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        // 숫자 키패드 텍스트 설정
        holder.itemBtn.text = keyPadData[position]

        if (holder.itemBtn.text == "왼") {
            // 하단 왼쪽 키패드: 지우기
            holder.itemBtn.visibility = View.GONE
            holder.itemimg01.visibility = View.VISIBLE
        } else if (holder.itemBtn.text == "오") {
            // 하단 오른쪽 키패드: 없음
            holder.itemBtn.visibility = View.GONE
            holder.itemimg02.visibility = View.VISIBLE
            holder.itemback.visibility=View.VISIBLE
        }

        /**
         * 숫자 버튼 클릭 리스너
         */
        holder.itemBtn.setOnClickListener {
            if (mListener != null) {
                mListener?.onNumClickEvent(holder.itemBtn.text.toString())
            }
        }

        /**
         * 지움 버튼 클릭 리스너
         */
        holder.itemimg02.setOnClickListener {
            if (mListener != null) {
                mListener?.onEraserClickEvent()
            }
        }

        return view!!
    }

    inner class ViewHolder {
        lateinit var itemBtn: Button
        lateinit var itemimg01: Button
        lateinit var itemimg02: Button
        lateinit var itemback: ImageView
    }
}
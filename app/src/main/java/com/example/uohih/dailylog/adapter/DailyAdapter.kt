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
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.main.UpdateActivity
import com.example.uohih.dailylog.view.CustomListDialog
import com.example.uohih.dailylog.view.ListViewAdapter
import org.json.JSONObject

class DailyAdapter(private val mContext: Context, private val dailyList: ArrayList<DBData>, private val delete: Boolean) : BaseAdapter() {

    private val base = DLogBaseApplication()
    private var selected = ArrayList<Boolean>(count) // 체크 된 항목
    var mListener: mCheckboxListener? = null // 전체 선택 해제 리스너
    var down = true


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

    interface mCheckboxListener {
        fun onmClickEvent()
    }

    fun setmCheckboxListener(listener: mCheckboxListener) {
        this.mListener = listener
    }

    /**
     * 데이터 삭제
     */
    fun removeAt(position: Int) {
        dailyList.removeAt(position)
        notifyDataSetChanged()
    }

    /**
     * 체크 된 항목
     */
    fun check() {
        var array = ArrayList<String>()
        for (i in 0 until count) {
            if (selected[i]) {
                base.setCheckOnItem(i.toString(), dailyList[i].no.toString())
            } else {
                base.setCheckOffItem(i.toString())
            }
        }

        var int = 0
        for (i in 0 until count) {
            if (!DLogBaseApplication().getCheckItem().optString(i.toString()).isNullOrEmpty()) {
                array.add(int, base.getCheckItem().get(i.toString()).toString())
                int++
            }
        }
        base.setDeleteItem(array)
    }


    override fun getItem(position: Int): DBData {
        return dailyList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dailyList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var holder: ViewHolder
        var view = convertView
        if (view == null) {
            holder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.daily_item_recycler_view, parent, false)
            holder.itemTitle = view.findViewById<TextView>(R.id.tv_daily_item_title)
            holder.itemContent = view.findViewById<TextView>(R.id.tv_daily_item_content)
            holder.itemBtn = view.findViewById<ImageButton>(R.id.btn_daily_item)
            holder.itemCheck = view.findViewById<CheckBox>(R.id.check_daily_item)
            holder.itemSrcLin = view.findViewById<LinearLayout>(R.id.scr_lin_daily_item)
            holder.itemSrc = view.findViewById<ScrollView>(R.id.scr_daily_item)
            holder.layout = view.findViewById<LinearLayout>(R.id.lin_daily_item)

            view.tag = holder
            for (i in 0 until count) {
                selected.add(false)
            }
        } else {
            holder = view.tag as ViewHolder
        }

        holder.itemTitle.text = dailyList[position].title
        holder.itemContent.text = dailyList[position].content
        var no = dailyList[position].no


        if (delete) {
            holder.itemBtn.visibility = View.GONE
            holder.itemCheck.visibility = View.VISIBLE
        } else {
            holder.itemCheck.visibility = View.GONE
            holder.itemBtn.visibility = View.VISIBLE
        }


        /**
         * 상세 내용이 없을 때
         */
        if (dailyList[position].content.isBlank()) {
            holder.itemBtn.visibility = View.GONE
        }

        /**
         * 텍스트뷰 터치 이벤트 죽이기
         */
        holder.itemContent.setOnTouchListener { v, event ->
            // TODO Auto-generated method stub
            holder.itemSrc.requestDisallowInterceptTouchEvent(true)
            //스크롤뷰가 텍스트뷰의 터치이벤트를 가져가지 못하게 함
            false
        }


        /**
         * 레이아웃 클릭 이벤트
         */
        holder.layout.setOnClickListener {
            if (holder.itemCheck.visibility == View.VISIBLE) {
                if (holder.itemCheck.isChecked) {
                    selected[position] = false
                } else {
                    // 전체 선택 해제 리스너
                    if (mListener != null) {
                        mListener?.onmClickEvent()
                    }
                    selected[position] = true
                }

            } else if (holder.itemBtn.visibility == View.VISIBLE) {
                if (down) {
                    holder.itemSrcLin.visibility = View.VISIBLE
                    holder.itemBtn.setBackgroundResource(R.drawable.btn_up_selector)
                    down = false
                } else {
                    holder.itemSrcLin.visibility = View.GONE
                    holder.itemBtn.setBackgroundResource(R.drawable.btn_down_selector)
                    down = true
                }
            }
        }


        /**
         * 레이아웃 롱클릭 이벤트
         */
        holder.layout.setOnLongClickListener {
            val listViewAdapter = ListViewAdapter((mContext as Activity), ArrayList())
            listViewAdapter.setContent(it.resources.getString(R.string.menu_05))
            listViewAdapter.setContent(it.resources.getString(R.string.menu_06))


            var customDialogList = CustomListDialog(mContext, android.R.style.Theme_Material_Dialog_MinWidth)
            customDialogList = customDialogList.showDialogList(mContext, holder.itemTitle.text.toString(), DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int -> }, listViewAdapter, AdapterView.OnItemClickListener { parent, view, position, id ->
                when (position) {
                    0 -> { //수정
                        var jsonObject = JSONObject()
                        jsonObject.put("no", dailyList[position].no)
                        jsonObject.put("date", dailyList[position].date)
                        jsonObject.put("title", dailyList[position].title)
                        jsonObject.put("content", dailyList[position].content)

                        val intent = Intent(mContext, UpdateActivity::class.java)
                        intent.putExtra("daily", jsonObject.toString())
                        mContext.startActivity(intent)
                        Toast.makeText(mContext, "수정", Toast.LENGTH_SHORT).show()
                    }
                    1 -> { //삭제
                        DBHelper(mContext).delete(arrayListOf(no.toString()), "no")
                        removeAt(position)
                        Toast.makeText(mContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                    }

                }
                customDialogList.dismiss()

            })!!
            customDialogList.show()

            return@setOnLongClickListener true
        }

        /**
         * 화살표 클릭 이벤트
         */
        holder.itemBtn.setOnClickListener {
            if (down) {
                holder.itemSrcLin.visibility = View.VISIBLE
                holder.itemBtn.setBackgroundResource(R.drawable.btn_up_selector)
                down = false
            } else {
                holder.itemSrcLin.visibility = View.GONE
                holder.itemBtn.setBackgroundResource(R.drawable.btn_down_selector)
                down = true
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
                notifyDataSetChanged()
            }
        }



        if (selected.isNotEmpty()) {
            holder.itemCheck.isChecked = selected[position]
        } else {
            holder.itemCheck.isChecked = false
        }

        // 체크 상태
        check()

        return view!!
    }

    inner class ViewHolder {
        lateinit var itemTitle: TextView
        lateinit var itemContent: TextView
        lateinit var itemBtn: ImageButton
        lateinit var itemCheck: CheckBox
        lateinit var itemSrcLin: LinearLayout
        lateinit var itemSrc: ScrollView
        lateinit var layout: LinearLayout
    }


}


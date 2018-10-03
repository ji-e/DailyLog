package com.example.uohih.dailylog.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.example.uohih.dailylog.R
import kotlinx.android.synthetic.main.dialog_list.view.*


class CustomListDialog(context: Activity, theme: Int) : Dialog(context, theme) {

    class Builder(private val context: Context) {
        private var dialogTitle: String? = null
        private var close = false

        private var mClosebtnClickListener: DialogInterface.OnClickListener? = null
        private var mItemClickListener: AdapterView.OnItemClickListener? = null

        private var listAdapter: ListAdapter? = null
        private var listView: ListView? = null

        /**
         * 닫기 버튼 리스너
         */
        fun setmCloseBtnClickListener(text: String?, mClosebtnClickListener: DialogInterface.OnClickListener?): Builder {
            if (mClosebtnClickListener != null) {
                close = true
            }
            dialogTitle = text
            this.mClosebtnClickListener = mClosebtnClickListener
            return this
        }

        /**
         * 리스트뷰 아이템 리스너
         */
        fun setmItemClickListener(mItemClickListener: AdapterView.OnItemClickListener): Builder {
            this.mItemClickListener = mItemClickListener
            return this
        }

        /**
         * 리스트 뷰
         */
        fun setListAdater(listAdapter: ListAdapter): Builder {
            this.listAdapter = listAdapter
            return this
        }


        /**
         * 커스텀 다이얼로그 생성
         */
        fun create(): CustomListDialog {
            val dialog = CustomListDialog((context as Activity), android.R.style.Theme_Material_Dialog_MinWidth)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

            dialog.addContentView(contentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

            // title 세팅
            if (dialogTitle.isNullOrEmpty()) {
                contentView.daiog_list_tv_title.visibility = View.GONE
                contentView.daiog_list_line.visibility = View.GONE
            } else {
                contentView.daiog_list_tv_title.text = dialogTitle
            }

            // 닫기 버튼
            if (!close) {
                contentView.daiog_list_btn_close.visibility = View.GONE
            } else {
                contentView.daiog_list_btn_close.setOnClickListener {
                    dialog.dismiss()
                }
            }


            if (dialogTitle.isNullOrEmpty() and !close) {
                contentView.daiog_list_title.visibility = View.GONE
            }

            // 리스트뷰 세팅
            listView = contentView.dailog_list
            listView?.adapter = listAdapter
            listView?.onItemClickListener = mItemClickListener




            return dialog
        }

    }

    /**
     * 리스트 다이얼로그
     */
    fun showDialogList(context: Context?, listAdapter: ListAdapter, itemClickListener: AdapterView.OnItemClickListener) {
        showDialogList(context, null, null, listAdapter, itemClickListener)
    }

    /**
     * 타이틀 리스트 다이얼로그
     */
    fun showDialogList(context: Context?, title: String?, closeBtnListener: DialogInterface.OnClickListener?, listAdapter: ListAdapter, itemClickListener: AdapterView.OnItemClickListener): CustomListDialog? {
        if (context == null) {
            return null
        }

        if (context is Activity) {
            val activity = context as Activity?

            if (activity!!.isFinishing || activity.isDestroyed) {
                return null
            }
        }


        val builder = CustomListDialog.Builder(context)
        builder.setListAdater(listAdapter)
        builder.setmCloseBtnClickListener(title, closeBtnListener)
        builder.setmItemClickListener(itemClickListener)
        val dialog = builder.create()
//        dialog.show()
        return dialog
    }


}

class ListViewAdapter(context: Context, item: ArrayList<String>) : BaseAdapter() {
    private val mContext = context
    private val mItem = item


    // 리스트에 값을 추가할 메소드
    fun setContent(text: String) {
        mItem.add(text)
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        lateinit var viewHolder: ViewHolder
        var convertView = view

        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_list_item, parent, false)
            viewHolder.textView = convertView.findViewById(R.id.dailog_list_item)
            convertView.tag = viewHolder
            viewHolder.textView.text = mItem[position]

            return convertView
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.textView.text = mItem[position]

        return convertView
    }

    override fun getItem(position: Int) = mItem[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mItem.size

    inner class ViewHolder {
        lateinit var textView: TextView
    }

}
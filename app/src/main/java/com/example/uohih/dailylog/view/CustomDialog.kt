package com.example.uohih.dailylog.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.*
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.LogUtil
import kotlinx.android.synthetic.main.dialog_basic.view.*

/**
 * 다이얼로그
 * context: Context
 * theme: Int
 */
class CustomDialog(context: Context, theme: Int) : Dialog(context, theme) {

    class Builder(private val context: Context) {
        private var dialogText: String? = null
        private var dialogBtnNoText: String? = null
        private var dialogBtnYesText: String? = null

        private var mNoBtnClickListener: DialogInterface.OnClickListener? = null
        private var mYesBtnClickListener: DialogInterface.OnClickListener? = null

        /**
         * 메세지 세팅
         */
        fun setMsg(text: String): Builder {
            dialogText = text
            return this
        }

        /**
         * 취소 버튼 리스너
         */
        fun setmNoBtnClickListener(text: String?, mNoBtnClickListener: DialogInterface.OnClickListener?): Builder {
//            dialog.dialogBtnNo.setOnClickListener(mNoBtnClickListener)
            dialogBtnNoText = text
            this.mNoBtnClickListener = mNoBtnClickListener
            return this
        }

        /**
         * 확인 버튼 리스너
         */
        fun setmYesBtnClickListener(text: String, mYesBtnClickListener: DialogInterface.OnClickListener?): Builder {
//            dialog.dialogBtnYes.setOnClickListener(mYesBtnClickListener)
            dialogBtnYesText = text
            this.mYesBtnClickListener = mYesBtnClickListener
            return this
        }

        /**
         * 커스텀 다이얼로그 생성
         */
        fun create(): CustomDialog {
            val dialog = CustomDialog(context, android.R.style.Theme_Material_Dialog_MinWidth)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_basic, null)

            dialog.addContentView(contentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))


            // 팝업 메세지 세팅
            if (!dialogText.isNullOrEmpty()) {
                contentView.dialog_text.text = dialogText
            }

            // 취소 버튼
            if (dialogBtnNoText.isNullOrEmpty()) {
                contentView.dialog_no.visibility = View.GONE
            } else {
                contentView.dialog_no.text = dialogBtnNoText
                if(mNoBtnClickListener!=null){
                    contentView.dialog_no.setOnClickListener {
                        mNoBtnClickListener!!.onClick(dialog, DialogInterface.BUTTON_NEGATIVE)
                        dialog.dismiss()
                    }

//                    contentView.dialog_no.setOnClickListener(mNoBtnClickListener)
                }else{
                    contentView.dialog_no.setOnClickListener {
                        dialog.dismiss()
                    }
                }

            }

            // 확인 버튼
            if (dialogBtnYesText.isNullOrEmpty()) {
                contentView.dialog_yes.visibility = View.GONE
            } else {
                contentView.dialog_yes.text = dialogBtnYesText
                if(mYesBtnClickListener!=null){
                    contentView.dialog_yes.setOnClickListener {
                        mYesBtnClickListener!!.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                        dialog.dismiss()
                    }
//                    contentView.dialog_yes.setOnClickListener(mYesBtnClickListener)
                }else{
                    contentView.dialog_yes.setOnClickListener {
                        dialog.dismiss()
                    }
                }

            }



            return dialog
        }

    }


    /**
     * 확인 버튼 다이얼로그
     */
    fun showDialog(context: Context?, msg: String, yesBtnTxt: String, yesBtnListener: DialogInterface.OnClickListener?) {
        showDialog(context, msg, null, null, yesBtnTxt, yesBtnListener)
    }

    /**
     * 취소, 확인 버튼 다이얼로그
     */
    fun showDialog(context: Context?, msg: String, pnoBtnTxt: String?, noBtnListener: DialogInterface.OnClickListener?, yesBtnTxt: String, yesBtnListener: DialogInterface.OnClickListener?): CustomDialog? {
        if (context == null) {
            return null
        }

        if (context is Activity) {
            val activity = context as Activity?

            if (activity!!.isFinishing || activity.isDestroyed) {
                return null
            }
        }

        val builder = CustomDialog.Builder(context)
        builder.setMsg(msg)
        builder.setmNoBtnClickListener(pnoBtnTxt, noBtnListener)
        builder.setmYesBtnClickListener(yesBtnTxt, yesBtnListener)
        val dialog = builder.create()
        dialog.show()
        return dialog
    }



}
package com.example.uohih.dailylog.setting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.adapter.KeyPadAdapter
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.LogUtil
import kotlinx.android.synthetic.main.activity_password_setting.*

class PasswordSettingActivity : DLogBaseActivity() {
    private val mIvPwResId = intArrayOf(R.id.iv_pin0, R.id.iv_pin1, R.id.iv_pin2, R.id.iv_pin3, R.id.iv_pin4, R.id.iv_pin5)
    private var mIvPw = arrayOfNulls<ImageView>(mIvPwResId.size)
    private var str: String = ""
//    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_setting)

        /**
         * 기존 비밀번호 있을 시 비밀번홓 확인
         */
        if (getPreference(passwordSetting) != "") {
            pwsetting_tv.text = getString(R.string.pwsetting_text_confirm)
        }

        // 상단바 닫기 버튼
        pwsetting_title_view.setClose()
//        pwsetting_title_view.setCloseBtnClickListener(View.OnClickListener {
////
////        })

        // 핀 클릭 리스너
        pwsetting_linear_pin_input.setOnClickListener {
            pwsetting_input.text = ""
        }

        /**
         * pin id 세팅
         */
        for (i in 0 until mIvPwResId.size) {
            val view: ImageView = pwsetting_linear_pin_input.findViewById(mIvPwResId[i])
            mIvPw[i] = view
            LogUtil.d(mIvPw[i].toString())
        }


        // 비밀번호 입력 할 때 마다 리스너
        pwsetting_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setPwImage(count)
                if (count == mIvPwResId.size) {
                    setPassword()
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })


        val mAadapter = KeyPadAdapter(this, setkeyPadData())
        keypad_grid.adapter = mAadapter

        /**
         * 키패드 클릭 리스너
         */
        mAadapter?.setmKeyPadListener(object : KeyPadAdapter.mKeyPadListener {
            override fun onEraserClickEvent() {
                str = removeLastChar()
                pwsetting_input.text = str
            }

            override fun onNumClickEvent(index: String) {
                str += index
                pwsetting_input.text = str
            }
        })
    }


    /**
     * 키패드 클릭시 pin 색 변환
     */
    private fun setPwImage(inputLen: Int) {
        for (i in 0 until mIvPw.size) {
            if (mIvPw[i] != null) {
                mIvPw[i]?.isSelected = i < inputLen
            }
        }
    }

    /**
     * 지움 버튼 클릭시 마지막 글자 지움
     */
    private fun removeLastChar(): String {
        if (str.isNotEmpty()) {
            return str.substring(0, str.length - 1)
        }
        return str
    }

    /**
     * 비밀 번호 6자리 입력시
     */
    fun setPassword() {
        LogUtil.d(str)
        if (pwsetting_tv.text == getString(R.string.pwsetting_text01)) {
            setPreference(temp, str)
            pwsetting_tv.text = getString(R.string.pwsetting_text02)
            pwsetting_input.text = ""
            str = ""
        } else if (pwsetting_tv.text == getString(R.string.pwsetting_text02)) {
            val pw = getPreference(temp)
            if (pw == str) {
                Toast.makeText(this, getString(R.string.pwsetting_text_complete), Toast.LENGTH_SHORT).show()
                setPreference(activitySetting, str)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.pwsetting_text_inconsistency), Toast.LENGTH_SHORT).show()
                pwsetting_tv.text = getString(R.string.pwsetting_text01)
                setPreference(passwordSetting, "")
                pwsetting_input.text = ""
                str = ""
            }
        } else {
            val pw = getPreference(passwordSetting)
            if (pw == str) {
                pwsetting_tv.text = getString(R.string.pwsetting_text01)
            } else {
                Toast.makeText(this, getString(R.string.pwsetting_text_cInconsistency), Toast.LENGTH_SHORT).show()
            }
            pwsetting_input.text = ""
            str = ""
        }

    }
}

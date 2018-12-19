package com.example.uohih.dailylog.setting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.database.DBHelper
import kotlinx.android.synthetic.main.activity_setting_reset.*

class SettingResetActivity : DLogBaseActivity() {

    private val db = DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_reset)

        // 상단바 닫기 버튼
        reset_title_view.setClose()


        /**
         * 비밀번호 초기화 클릭 리스너
         */
        reset_password.setOnClickListener {
            val intent = Intent(this, PasswordCheckActivity::class.java)
            intent.putExtra("reset", true)
            startActivity(intent)
        }

        /**
         * 데이터 초기화 클릭 리스너
         */
        reset_data.setOnClickListener {
            db.onReset()
            Toast.makeText(this, "데이처 초기화가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }


    }


}

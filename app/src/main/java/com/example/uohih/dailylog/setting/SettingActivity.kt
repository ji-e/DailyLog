package com.example.uohih.dailylog.setting

import android.content.Intent
import android.os.Bundle
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : DLogBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // 상단바 닫기 버튼
        setting_title_view.setClose()

        // 현재 버전 정보 세팅
        setting_cur_version.text=getVersionInfo()

        // 제작자 세팅
        setting_development.text="Ji.E"

        /**
         * 비밀번호 설정 클릭 리스너
         */
        setting_password.setOnClickListener{
            val intent = Intent(this, PasswordSettingActivity::class.java)
            startActivity(intent)
            finish()
        }

        /**
         * 엑셀로 내보내기 클릭 리스너
         */
        setting_excel.setOnClickListener {


        }


        /**
         * 초기화 클릭 리스너
         */
        setting_reset.setOnClickListener {


        }

    }



}

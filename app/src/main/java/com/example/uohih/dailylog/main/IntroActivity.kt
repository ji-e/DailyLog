package com.example.uohih.dailylog.main

import android.content.Intent
import android.os.Bundle
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.setting.PasswordCheckActivity
import kotlinx.android.synthetic.main.activity_intro.*


class IntroActivity : DLogBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        intro_activity.postDelayed({
            if (getPreference(passwordSetting) != "") {
                var intent = Intent(this, PasswordCheckActivity::class.java)
                startActivityForResult(intent, passwordCheck)
            } else {
                goPage()
            }
        }, 2000)
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                passwordCheck -> {
                    goPage()
                }
            }
        } else {
            exit()
        }


    }

    private fun goPage() {
        // 프리퍼런스에 저장된 액티비티 값 가져오기
        val result = getPreference(activitySetting)

        //2초후 저장된 액티비티로 이동
        var intent: Intent? = null
        when (result) {
            "daily" -> intent = Intent(this, DailyActivity::class.java)
            "weekly" -> intent = Intent(this, WeeklyActivity::class.java)
            "monthly" -> intent = Intent(this, MonthlyActivity::class.java)
            else -> intent = Intent(this, DailyActivity::class.java)
        }
        startActivity(intent)
        finish()

    }
}

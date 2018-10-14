package com.example.uohih.dailylog.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.R
import kotlinx.android.synthetic.main.activity_intro.*
import android.R.attr.key


class IntroActivity : DLogBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


    }

    override fun onResume() {
        super.onResume()


        // 프리퍼런스에 저장된 액티비티 값 가져오기
        val result = getPreference(activitySetting)

        //2초후 저장된 액티비티로 이동
        intro_activity.postDelayed({
            var intent: Intent? = null
            when (result) {
                "daily" -> intent = Intent(this, DailyActivity::class.java)
                "weekly" -> intent = Intent(this, WeeklyActivity::class.java)
                "monthly" -> intent = Intent(this, MonthlyActivity::class.java)
                else -> intent = Intent(this, DailyActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}

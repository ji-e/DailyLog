package com.example.uohih.dailylog.main

import android.content.Intent
import android.os.Bundle
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : DLogBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }

    override fun onResume() {
        super.onResume()

        //2초후 DailyActivity로 이동
        intro_activity.postDelayed({
            val intent = Intent(this, DailyActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}

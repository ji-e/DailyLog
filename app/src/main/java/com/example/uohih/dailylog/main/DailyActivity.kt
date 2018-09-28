package com.example.uohih.dailylog.main

import android.os.Bundle
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity

class DailyActivity : DLogBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)
    }
}

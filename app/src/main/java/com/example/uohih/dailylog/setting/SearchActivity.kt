package com.example.uohih.dailylog.setting

import android.app.Activity
import android.os.Bundle
import com.example.uohih.dailylog.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // 상단바 닫기 버튼
        search_title_view.setClose()
    }
}

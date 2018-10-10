package com.example.uohih.dailylog.main

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.widget.TextView
import android.widget.Toast
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.database.DBHelper
import kotlinx.android.synthetic.main.activity_write.*
import org.json.JSONObject

class WriteActivity : DLogBaseActivity() {
    private val base = DLogBaseApplication()
    //    private var jsonCalendar = JSONObject(getToday().toString())
    private var jsonCalendar = base.getDateInfom()
    private val currentDate = getToday().get("yyyymmdd").toString()
    private var date = jsonCalendar.get("yyyymmdd").toString()
    private val db = DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        // 상단바 닫기 버튼
        write_top_title_view.setClose()

        // 현재 날짜 세팅
        this.write_tv_date.text = String.format(getString(R.string.daily_date), jsonCalendar.get("year"), jsonCalendar.get("month"), jsonCalendar.get("date"), jsonCalendar.get("day"))
        DLogBaseApplication().setDateInfom(jsonCalendar)

        /**
         * 제목 입력에 따른 삭제 버튼
         */
        write_et_title.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (write_et_title.text.length > 0)
                    write_btn_delete.visibility = View.VISIBLE
            } else {
                write_btn_delete.visibility = View.GONE
            }

        }

        /**
         * 제목 입력에 따른 삭제 버튼
         */
        write_et_title.setOnEditorActionListener { v, actionId, event ->
            if (actionId == IME_ACTION_NEXT) {
                    write_et_content.requestFocus()
            }
            false
        }

        /**
         * 제목 입력에 따른 삭제 버튼
         */
        write_et_title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count == 0 && start == 0) {
                    write_btn_delete.visibility = View.GONE
                } else {
                    write_btn_delete.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

        /**
         * 삭제 버튼 클릭 리스너
         */
        write_btn_delete.setOnClickListener {
            write_et_title.setText("")
        }


        // 일지 쓰기 버튼 클릭 이벤트
        write_btn_write.setOnClickListener {
            if (write_et_title.text.toString().isEmpty()) {
                emptyAlert()
            } else {
                db.insert(date.toInt(), write_et_title.text.toString(), write_et_content.text.toString())
                finish()
            }
        }

        // 이전 버튼 클릭 이벤트
        write_btn_back.setOnClickListener {
            var preCalendar = JSONObject(getDate(true, 1, "일").toString())
            DLogBaseApplication().setDateInfom(preCalendar)
            write_tv_date.text = String.format(getString(R.string.daily_date), preCalendar.get("year"), preCalendar.get("month"), preCalendar.get("date"), preCalendar.get("day"))
            write_et_title.setText("")
            write_et_content.setText("")

            date = preCalendar.get("yyyymmdd").toString()
        }

        // 다음 버튼 클릭 이벤트
        write_btn_next.setOnClickListener {
            var nextCalendar = JSONObject(getDate(false, 1, "일").toString())
            DLogBaseApplication().setDateInfom(nextCalendar)
            write_tv_date.text = String.format(getString(R.string.daily_date), nextCalendar.get("year"), nextCalendar.get("month"), nextCalendar.get("date"), nextCalendar.get("day"))
            write_et_title.setText("")
            write_et_content.setText("")

            date = nextCalendar.get("yyyymmdd").toString()
        }

    }

    /**
     * 제목은 null이면 안됨
     */
    private fun emptyAlert() {
        //todo alert 일단 toast
        Toast.makeText(this, "제목 써!", Toast.LENGTH_SHORT).show()
    }
}

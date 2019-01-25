package com.example.uohih.dailylog.setting

import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.EventLog
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.adapter.DBData
import com.example.uohih.dailylog.adapter.DailyAdapter
import com.example.uohih.dailylog.adapter.SearchAdapter
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.view.CalendarDialog
import com.example.uohih.dailylog.view.CustomDialog
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_update.*
import org.json.JSONObject
import org.w3c.dom.Text

/**
 * 일지 검색
 */
class SearchActivity : DLogBaseActivity() {

    private val base = DLogBaseApplication()
    private var mAadapter: SearchAdapter? = null
    private var searchList = arrayListOf<DBData>()
    private val db = DBHelper(this)
    private var seleteDate = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // 상단바 닫기 버튼
        search_title_view.setClose()

        search_log_date1_tv.hint = getToday().get("yyyymmdd").toString()
        search_log_date2_tv.hint = getToday().get("yyyymmdd").toString()


        /**
         * 일지 검색 체크박스
         */
        search_check_log.isChecked = true
        search_check_log.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                search_layout_log.visibility = View.VISIBLE
                search_check_date.isChecked = false
                search_result_tv.visibility = View.VISIBLE
                search_listview.visibility = View.GONE
                search_btn_confirm.visibility = View.VISIBLE
                search_log_date1_tv.text=""
                search_log_date2_tv.text=""
                search_log_date1_tv.hint = getToday().get("yyyymmdd").toString()
                search_log_date2_tv.hint = getToday().get("yyyymmdd").toString()
            } else {
                search_check_date.isChecked = true
                search_layout_log.visibility = View.GONE
            }
        }

        search_layout.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (search_edt.text.isNotEmpty())
                    search_btn_delete.visibility = View.VISIBLE
            } else {
                search_btn_delete.visibility = View.GONE
            }
        }


        /**
         * 입력에 따른 삭제 버튼
         */
        search_edt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == IME_ACTION_NEXT) {
                search_layout.requestFocus()
            }
            false
        }

        /**
         * 입력에 따른 삭제 버튼
         */
        search_edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count == 0 && start == 0) {
                    search_btn_delete.visibility = View.GONE
                } else {
                    search_btn_delete.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        /**
         * 삭제 버튼 클릭 리스너
         */
        search_btn_delete.setOnClickListener {
            search_edt.setText("")
        }


        /**
         * 일지 검색 캘린더1 클릭
         */
        search_log_date1.setOnClickListener {
            var calendarDialog = CalendarDialog(this, android.R.style.Theme_Material_Dialog_MinWidth)
            if (!search_log_date1_tv.text.isNullOrEmpty()) {
                var date = search_log_date1_tv.text.toString().substring(0, 4) + "-" + search_log_date1_tv.text.toString().substring(4, 6) + "-" + search_log_date1_tv.text.toString().substring(6)
                calendarDialog = calendarDialog.showDialogCalendar(this, date)!!
            } else {
                calendarDialog = calendarDialog.showDialogCalendar(this, null)!!
            }

            calendarDialog.show()

            calendarDialog.setOnDismissListener {
                search_log_date1_tv.text = base.getSeleteDate()
            }
            search_layout.requestFocus()
        }


        /**
         * 일지 검색 캘린더2 클릭
         */
        search_log_date2.setOnClickListener {
            var calendarDialog = CalendarDialog(this, android.R.style.Theme_Material_Dialog_MinWidth)
            if (!search_log_date2_tv.text.isNullOrEmpty()) {
                var date = search_log_date2_tv.text.toString().substring(0, 4) + "-" + search_log_date2_tv.text.toString().substring(4, 6) + "-" + search_log_date2_tv.text.toString().substring(6)
                calendarDialog = calendarDialog.showDialogCalendar(this, date)!!
            } else {
                calendarDialog = calendarDialog.showDialogCalendar(this, null)!!
            }
            calendarDialog.show()

            calendarDialog.setOnDismissListener {
                search_log_date2_tv.text = base.getSeleteDate()
            }
            search_layout.requestFocus()
        }

        /**
         * 검색 버튼 클릭
         */
        search_btn_confirm.setOnClickListener {
            if (search_log_date1_tv.text.toString().isNullOrEmpty() || search_log_date2_tv.text.toString().isNullOrEmpty()) {
                getSearchResult(1)
            } else {
                getSearchResult(2)
            }
            search_layout.requestFocus()
        }


        /**
         * 날짜 검색 체크박스
         */
        search_check_date.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                search_check_log.isChecked = false
                search_layout_date.visibility = View.VISIBLE
                search_btn_confirm.visibility = View.GONE
                var jsonCalendar = JSONObject(getToday().toString())
                search_date1_tv.text = jsonCalendar.get("yyyymmdd").toString()
//                search_date1_tv.text = base.getSeleteDate()
                getSearchResult(3)
            } else {
                search_layout_date.visibility = View.GONE
                search_check_log.isChecked = true
            }
        }

        /**
         * 날짜 검색 캘린더 클릭
         */
        search_date1.setOnClickListener {
            var calendarDialog = CalendarDialog(this, android.R.style.Theme_Material_Dialog_MinWidth)
            if (!search_date1_tv.text.isNullOrEmpty()) {
                var date = search_date1_tv.text.toString().substring(0, 4) + "-" + search_date1_tv.text.toString().substring(4, 6) + "-" + search_date1_tv.text.toString().substring(6)
                calendarDialog = calendarDialog.showDialogCalendar(this, date)!!
            } else {
                calendarDialog = calendarDialog.showDialogCalendar(this, null)!!
            }
            calendarDialog.show()

            calendarDialog.setOnDismissListener {
                search_date1_tv.text = base.getSeleteDate()
                getSearchResult(3)
            }
        }

        search_edt.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeypad()
            }
        }


        /**
         * 검색결과 리스트
         */
        mAadapter = SearchAdapter(this, searchList, true)
        search_listview.adapter = mAadapter


    }


    /**
     * 키보드 숨기기
     */
    private fun hideKeypad() {
        var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(search_edt.windowToken, 0)
        search_layout.requestFocus()
    }


    /**
     * 검색 결과 가져오기
     * index: Int (1: 검색어 검색, 2: 검색어 및 날짜 검색, 3: 날짜 검색)
     */
    fun getSearchResult(index: Int) {
        seleteDate = base.getSeleteDate().toInt()


        var cursor = db.select("")

        when (index) {
            1 -> {
                if (search_edt.text.isNullOrEmpty()) {
                    val customDialog = CustomDialog(this, android.R.style.Theme_Material_Dialog_MinWidth)
                    customDialog.showDialog(this, resources.getString(R.string.search_msg2), resources.getString(R.string.btn_01), null)
                    return
                }
                cursor = db.select(search_edt.text.toString())
            }
            2 -> {
                if (search_edt.text.isNullOrEmpty()) {
                    val customDialog = CustomDialog(this, android.R.style.Theme_Material_Dialog_MinWidth)
                    customDialog.showDialog(this, resources.getString(R.string.search_msg2), resources.getString(R.string.btn_01), null)
                    return
                }
                if (search_log_date1_tv.text.isNullOrEmpty()) {
                    val customDialog = CustomDialog(this, android.R.style.Theme_Material_Dialog_MinWidth)
                    customDialog.showDialog(this, resources.getString(R.string.search_msg3), resources.getString(R.string.btn_01), null)
                    return
                }
                if (search_log_date2_tv.text.isNullOrEmpty()) {
                    val customDialog = CustomDialog(this, android.R.style.Theme_Material_Dialog_MinWidth)
                    customDialog.showDialog(this, resources.getString(R.string.search_msg4), resources.getString(R.string.btn_01), null)
                    return
                }
                if (search_log_date2_tv.text.toString().toInt() < search_log_date1_tv.text.toString().toInt()) {
                    val customDialog = CustomDialog(this, android.R.style.Theme_Material_Dialog_MinWidth)
                    customDialog.showDialog(this, resources.getString(R.string.search_msg1), resources.getString(R.string.btn_01), null)
                    search_log_date2_tv.hint = getToday().get("yyyymmdd").toString()
                    search_log_date2_tv.text = ""
                    return
                }
                cursor = db.select(search_log_date1_tv.text.toString().toInt(), search_log_date2_tv.text.toString().toInt(), search_edt.text.toString())
            }
            3 -> {
                cursor = db.select(seleteDate)
            }
        }

        searchList.clear()

        while (cursor.moveToNext()) {
            searchList.add(DBData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
        }

        if (searchList.size <= 0) {
            search_result_tv.visibility = View.VISIBLE
            search_listview.visibility = View.GONE
        } else {
            search_result_tv.visibility = View.GONE
            search_listview.visibility = View.VISIBLE
        }

        mAadapter?.notifyDataSetChanged()
    }


}
package com.example.uohih.dailylog.view

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.adapter.DBData
import com.example.uohih.dailylog.adapter.DialogAdapter
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.database.DBHelper
import com.example.uohih.dailylog.main.*
import com.example.uohih.dailylog.setting.SearchActivity
import com.example.uohih.dailylog.setting.SettingActivity
import kotlinx.android.synthetic.main.dialog_menu.view.*
import kotlinx.android.synthetic.main.top_title_view.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


/**
 * 타이틀 바
 *
 */
class TopTitleView : RelativeLayout {

    enum class menu {
        PENCIL,
        LOGO,
        MENU,
        SEARCH,
        OPEN,
        TITLE,
        CALENDAR
    }

    private var mContext: Context? = null
    private var mRootView: View? = null


    private var mListener: mClickListener? = null
    interface mClickListener {
        fun onmClickEvent()
    }

    fun setmClickListener(listener: mClickListener) {
        this.mListener = listener
    }


    constructor(context: Context) : super(context) {
        init(null, 0, context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0, context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle, context)
    }


    /**
     * 닫기 버튼
     */
    private val mCloseBtnClickListener: View.OnClickListener = OnClickListener {
        if (mContext != null && mContext is Activity) {
            (mContext as Activity).finish()
        }
    }


    /**
     * 메뉴 버튼
     */
    private var mMenuBtnClickListener: View.OnClickListener = OnClickListener {
        val popupView = LayoutInflater.from(mContext).inflate(R.layout.dialog_menu, null)
        val mPopupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        mPopupWindow.isFocusable = true
        mPopupWindow.showAsDropDown(it)


        if (DLogBaseApplication().getMonthly()) {
            popupView.menu_delete.visibility = View.GONE
        }


        /**
         * 편집
         */
        popupView.menu_delete.setOnClickListener {
            setEraser()
            mPopupWindow.dismiss()
        }
        /**
         * 보기 방식
         */
        popupView.menu_view.setOnClickListener {

            val listViewAdapter = DialogAdapter((mContext as Activity), ArrayList())
            listViewAdapter.setContent(resources.getString(R.string.daily_title))
            listViewAdapter.setContent(resources.getString(R.string.weekly_title))
            listViewAdapter.setContent(resources.getString(R.string.monthly_title))

            var customDialogList = CustomListDialog((mContext as Activity), android.R.style.Theme_Material_Dialog_MinWidth)
            customDialogList = customDialogList.showDialogList(mContext, resources.getString(R.string.menu_02), DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int -> }, listViewAdapter, AdapterView.OnItemClickListener { parent, view, position, id ->
                when (position) {
                    0 -> { //일간
                        val intent = Intent(mContext, DailyActivity::class.java)
                        (mContext as Activity).startActivity(intent)
                        (mContext as Activity).finish()
                    }
                    1 -> { //주간
                        val intent = Intent(mContext, WeeklyActivity::class.java)
                        (mContext as Activity).startActivity(intent)
                        (mContext as Activity).finish()
                    }
                    2 -> { //월간
                        val intent = Intent(mContext, MonthlyActivity::class.java)
                        (mContext as Activity).startActivity(intent)
                        (mContext as Activity).finish()
                    }
                }
                postDelayed({
                    customDialogList.dismiss()
                }, 500)

            })!!
            customDialogList.show()

            mPopupWindow.dismiss()
        }

        popupView.menu_setting.setOnClickListener {
            val intent = Intent(mContext, SettingActivity::class.java)
            (mContext as Activity).startActivity(intent)
            mPopupWindow.dismiss()
        }

    }


    /**
     * 검색 버튼
     */
    private val mSearchBtnClickListener: View.OnClickListener = OnClickListener {

        // todo 다이얼로그 테스트
//        val customDialog = CustomDialog(mContext!!, android.R.style.Theme_Material_Dialog_MinWidth)
//        customDialog.showDialog(mContext, "fdfssssssssssd", "ㄹㅇㄹㅇㄹ", null, "fdafdf", null)

        val intent = Intent(mContext, SearchActivity::class.java)
        (mContext as Activity).startActivity(intent)


    }

    /**
     * 불러오기 버튼
     */
    private val mOpenBtnClickListener: View.OnClickListener = OnClickListener {

        var dailyList = arrayListOf<DBData>()
        var arrayList = arrayListOf<String>()

        val cursor = DBHelper(mContext as Activity).selectResent()
        dailyList.clear()
        while (cursor.moveToNext()) {
            if (!cursor.getString(2).isNullOrEmpty()) {
                dailyList.add(DBData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
                arrayList.add(cursor.getString(2))
            }
        }

        if (arrayList.size<=0) {
            val customDialog = CustomDialog((mContext as Activity), android.R.style.Theme_Material_Dialog_MinWidth)
            customDialog.showDialog((mContext as Activity), resources.getString(R.string.dailog_notting), resources.getString(R.string.btn_01), null)
            return@OnClickListener
        }
        val listViewAdapter = DialogAdapter((mContext as Activity), arrayList)

        var customDialogList = CustomListDialog(mContext as Activity, android.R.style.Theme_Material_Dialog_MinWidth)
        customDialogList = customDialogList.showDialogList(mContext, resources.getString(R.string.menu_07), DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int -> }, listViewAdapter, AdapterView.OnItemClickListener { parent, view, p, id ->
            LogUtil.d(dailyList[p].title)

            customDialogList.dismiss()
            var jsonObject = JSONObject()
            jsonObject.put("no", dailyList[p].no)
            jsonObject.put("date", dailyList[p].date)
            jsonObject.put("title", dailyList[p].title)
            jsonObject.put("content", dailyList[p].content)


            val intent = Intent(mContext, WriteActivity::class.java)
            intent.putExtra("daily", jsonObject.toString())
            (mContext as Activity).startActivity(intent)
            (mContext as Activity).overridePendingTransition(0, 0)
            (mContext as Activity).finish()
            Toast.makeText(mContext, "불러오기", Toast.LENGTH_SHORT).show()

//            DLogBaseApplication().setDateInfom(DLogBaseActivity().getToday(jsonObject.get("date").toString()))


        })!!

        customDialogList.show()


    }

    /**
     * 캘린더 버튼
     */
    private var mCalendarBtnClickListener: View.OnClickListener = OnClickListener {

    }

    /**
     * 연필 버튼
     * 일지 작성(WriteActivity 이동)
     */
    private val mPencilBtnClickListener: View.OnClickListener = OnClickListener {
        val intent = Intent(mContext, WriteActivity::class.java)
        (mContext as Activity).startActivity(intent)

    }

    /**
     * 지우개 버튼
     */
    private var mEraserBtnClickListener: View.OnClickListener = OnClickListener { }


    /**
     * 상단바 연필 -> 지우개
     */
    fun setEraser() {
        if (top_btn_pencil.visibility == View.GONE) {
            top_btn_pencil.visibility = View.VISIBLE
        }
        val title = top_tv_title.text.toString()
        top_btn_pencil.setImageResource(R.drawable.btn_eraser_selector)
        top_tv_title.text = resources.getString(R.string.menu_01)
        setClose()
        top_btn_logo.setOnClickListener {
            setLogo(title)
        }
        DLogBaseApplication().setAllCheckBox(true)
        if (mListener != null) {
            mListener?.onmClickEvent()
        }

    }

    /**
     * close -> logo로 변경
     * title:String (logo일 때 title 필요)
     */
    fun setLogo(title:String){
        top_btn_pencil.setImageResource(R.drawable.btn_pencil_selector)
        top_btn_logo.setImageResource(R.drawable.dlog_logo_01)
        top_tv_title.text =title
        top_btn_pencil.setOnClickListener(mPencilBtnClickListener)
        DLogBaseApplication().setAllCheckBox(false)

        if (mListener != null) {
            mListener?.onmClickEvent()
        }
    }

    /**
     * 상단바 로고 -> 닫기
     */
    fun setClose() {
        top_btn_logo.setImageResource(R.drawable.btn_close_selector)
        top_btn_logo.setOnClickListener(mCloseBtnClickListener)

    }



    /**
     * 메뉴 visibility 처리
     */
    fun setGone(index: menu) {
        when (index) {
            menu.CALENDAR -> top_btn_calendar.visibility = View.GONE
            menu.LOGO -> top_btn_logo.visibility = View.GONE
            menu.OPEN -> top_btn_open.visibility = View.GONE
            menu.PENCIL -> top_btn_pencil.visibility = View.GONE
            menu.TITLE -> top_tv_title.visibility = View.GONE
            menu.MENU -> top_btn_menu.visibility = View.GONE
        }
    }


    fun setCalendarBtnClickListener(mCalendarBtnClickListener: View.OnClickListener) {
        top_btn_calendar.setOnClickListener(mCalendarBtnClickListener)
    }

    fun setEraserBtnClickListener(mEraserBtnClickListener: View.OnClickListener) {
        top_btn_pencil.setOnClickListener(mEraserBtnClickListener)

    }

    fun setMenuBtnClickListener(mMenuBtnClickListener: View.OnClickListener) {
        top_btn_menu.setOnClickListener(mMenuBtnClickListener)
    }

    fun setCloseBtnClickListener(mCloseBtnClickListener: View.OnClickListener) {
        top_btn_logo.setOnClickListener(mCloseBtnClickListener)
    }

    fun setOpenrBtnClickListener(mOpenBtnClickListener: View.OnClickListener) {
        top_btn_open.setOnClickListener(mOpenBtnClickListener)
    }


    /**
     * 초기화
     */
    private fun init(attrs: AttributeSet?, defStyle: Int, context: Context) {
        mContext = context
        val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mRootView = inflater.inflate(R.layout.top_title_view, null)
        addView(mRootView, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT))


        // Load attributes
        val at = context.obtainStyledAttributes(attrs, R.styleable.TopTitleView, defStyle, 0)


        // 상단 바 로고
        if (!at.hasValue(R.styleable.TopTitleView_btnLogo)) {
            top_btn_logo.visibility = View.GONE
        } else {
            top_btn_logo.setImageResource(R.drawable.dlog_logo_01)
        }

        // 상단 바 타이틀
        if (!at.hasValue(R.styleable.TopTitleView_tvTitle)) {
            top_tv_title.visibility = View.GONE
        } else {
            top_tv_title.text = at.getText(R.styleable.TopTitleView_tvTitle)
        }


        // 상단 바 메뉴
        if (!at.hasValue(R.styleable.TopTitleView_btnMenu)) {
//            drawable = at.getResourceId(R.styleable.TopTitleView_btnMenu,R.drawable.logo_03)
//            ivLogo.setImageResource(drawable!!)

            top_btn_menu.visibility = View.GONE
        } else {
            top_btn_menu.setOnClickListener(mMenuBtnClickListener)
        }

        // 상단 바 검색
        if (!at.hasValue(R.styleable.TopTitleView_btnSearch)) {
            top_btn_search.visibility = View.GONE
        } else {
            top_btn_search.setOnClickListener(mSearchBtnClickListener)
        }

        // 상단 바 불러오기
        if (!at.hasValue(R.styleable.TopTitleView_btnOpen)) {
            top_btn_open.visibility = View.GONE
        } else {
            top_btn_open.setOnClickListener(mOpenBtnClickListener)
        }

        // 상단 바 캘린더
        if (!at.hasValue(R.styleable.TopTitleView_btnCalendar)) {
            top_btn_calendar.visibility = View.GONE
        } else {
            top_btn_calendar.setOnClickListener(mCalendarBtnClickListener)
            top_tv_calendar.text = getCurrentDate() // 현재 날짜 세팅
        }

        // 상단 바 연필
        if (!at.hasValue(R.styleable.TopTitleView_btnPencil)) {
            top_btn_pencil.visibility = View.GONE
        } else {
            top_btn_pencil.setImageResource(R.drawable.btn_pencil_selector)
            top_btn_pencil.setOnClickListener(mPencilBtnClickListener)
        }

    }


    /**
     * 현재 날짜 구하기
     */
    private fun getCurrentDate(): String {
        val instance: Calendar = Calendar.getInstance()
        val date: String = instance.get(Calendar.DATE).toString() //현재 날짜
        return date
    }




}


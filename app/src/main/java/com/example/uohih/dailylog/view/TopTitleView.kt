package com.example.uohih.dailylog.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.RelativeLayout
import com.example.uohih.dailylog.R
import kotlinx.android.synthetic.main.activity_daily.view.*
import kotlinx.android.synthetic.main.top_title_view.view.*


/**
 * TODO: document your custom view class.
 */
class TopTitleView : RelativeLayout {

    private var mContext: Context? = null
    private var mRootView: View? = null

//    private var _textTitle: String? = null

//    private var tvTitle: TextView = top_tv_title
//    private var ivLogo: ImageView = top_iv_logo
//    private var ivMenu: ImageButton = top_btn_menu
//    private var ivSearch: ImageButton = top_btn_search
//    private var ivOpen: ImageButton = top_btn_open
//    private var ivCalendar: ImageButton = top_btn_calendar
//    private var ivpencil: ImageButton = top_btn_pencil

    // 닫기
    private val mCloseBtnClickListener = OnClickListener {
        if (mContext != null && mContext is Activity) {
            (mContext as Activity).finish()
        }
    }

    // 메뉴
    private var mMenuBtnClickListener = OnClickListener { }

    // 검색
    private val mSearchBtnClickListener = OnClickListener { }

    // 불러오기
    private val mOpenBtnClickListener = OnClickListener { }

    // 캘린더
    private var mCalendarBtnClickListener = OnClickListener { }

    // 연필
    private val mPencilBtnClickListener = OnClickListener { }

    // 지우개
    private var mEraserBtnClickListener = OnClickListener { }


    constructor(context: Context) : super(context) {
        init(null, 0, context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0, context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle, context)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int, context: Context) {

        mContext = context
        val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mRootView = inflater.inflate(R.layout.top_title_view, null)
        addView(mRootView, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT))


        // Load attributes
        val at = context.obtainStyledAttributes(attrs, R.styleable.TopTitleView, defStyle, 0)


        // 상단 바 로고
        if (!at.hasValue(R.styleable.TopTitleView_imgLogo)) {
            top_iv_logo.visibility = View.GONE
        }

        // 상단 바 타이틀
        if (!at.hasValue(R.styleable.TopTitleView_tvTitle)) {
            top_tv_title.visibility = View.GONE
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
            top_layout_calendar.visibility = View.GONE
        } else {
            top_layout_calendar.setOnClickListener(mCalendarBtnClickListener)
            //todo 오늘 날짜 가져오는 것
            top_tv_calendar.text = "오늘 날짜"
        }

        // 상단 바 연필
        if (!at.hasValue(R.styleable.TopTitleView_btnPencil)) {
            top_btn_pencil.visibility = View.GONE
        } else {
            top_btn_pencil.setOnClickListener(mPencilBtnClickListener)
        }


//        at.recycle()


    }

    /**
     * 상단바 연필 -> 지우개
     */
    fun setEraser() {
        top_btn_pencil.setImageResource(R.drawable.btn_eraser_selector)
        top_btn_pencil.setOnClickListener(mEraserBtnClickListener)
    }

    /**
     * 상단바 로고 -> 닫기
     */
    fun setClose(){
        top_iv_logo.setImageResource(R.drawable.btn_close_selector)
        top_iv_logo.setOnClickListener(mCloseBtnClickListener)
    }


    fun setCalendarBtnClickListener(mCalendarBtnClickListener: View.OnClickListener) {
        this.mCalendarBtnClickListener = mCalendarBtnClickListener
    }

    fun setEraserBtnClickListener(mEraserBtnClickListener: View.OnClickListener) {
        this.mEraserBtnClickListener = mEraserBtnClickListener
    }

    fun setMenuBtnClickListener(mMenuBtnClickListener: View.OnClickListener) {
        this.mMenuBtnClickListener = mMenuBtnClickListener
    }


}

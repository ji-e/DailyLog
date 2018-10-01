package com.example.uohih.dailylog.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.res.TypedArrayUtils.getString
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.main.WriteActivity
import kotlinx.android.synthetic.main.top_title_view.view.*
import java.util.*
import android.widget.*
import com.example.uohih.dailylog.R.style.*
import kotlinx.android.synthetic.main.dialog_menu.view.*
import kotlin.collections.ArrayList


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

    /**
     * 닫기
     */
    private val mCloseBtnClickListener : View.OnClickListener = OnClickListener {
        if (mContext != null && mContext is Activity) {
            (mContext as Activity).finish()
        }
    }


    // 메뉴
    private var mMenuBtnClickListener : View.OnClickListener = OnClickListener {
        val popupView = LayoutInflater.from(mContext).inflate(R.layout.dialog_menu, null)
        val mPopupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        mPopupWindow.isFocusable = true
        mPopupWindow.showAsDropDown(it)

        // todo 화면 잠금이 없을 때는 로그아웃 표시 안함
        val logout=true
        if(logout){
            popupView.menu_logout.visibility=View.GONE
        }

        // todo 일단 모두 toast
        popupView.menu_logout.setOnClickListener {
            Toast.makeText(mContext, "로그아웃", Toast.LENGTH_SHORT).show()
            mPopupWindow.dismiss()
        }
        /**
         * 보기 방식
         */
        popupView.menu_view.setOnClickListener {
            var arrayList:ArrayList<String>?=null
            val listViewAdapter=ListViewAdapter(mContext!!, ArrayList())
            listViewAdapter.setContent(resources.getString(R.string.daily_title))
            listViewAdapter.setContent(resources.getString(R.string.monthly_title))
            listViewAdapter.setContent(resources.getString(R.string.year_title))

            val customDialogList=CustomListDialog(mContext!!,  android.R.style.Theme_Material_Dialog_MinWidth)
            customDialogList.showDialogList(mContext,listViewAdapter)

//            Toast.makeText(mContext, "보기방식", Toast.LENGTH_SHORT).show()
            mPopupWindow.dismiss()
        }
        popupView.menu_excel.setOnClickListener {
            Toast.makeText(mContext, "엑셀", Toast.LENGTH_SHORT).show()
            mPopupWindow.dismiss()
        }
        popupView.menu_setting.setOnClickListener {
            Toast.makeText(mContext, "환경설정", Toast.LENGTH_SHORT).show()
            mPopupWindow.dismiss()
        }

    }


    // 검색
    private val mSearchBtnClickListener : View.OnClickListener= OnClickListener {

        // todo 다이얼로그 테스트
        val customDialog=CustomDialog(mContext!!,  android.R.style.Theme_Material_Dialog_MinWidth)
       customDialog.showDialog(mContext,"fdfssssssssssd","ㄹㅇㄹㅇㄹ",null,"fdafdf",null)
    }

    // 불러오기
    private val mOpenBtnClickListener : View.OnClickListener = OnClickListener { }

    // 캘린더
    private var mCalendarBtnClickListener : View.OnClickListener = OnClickListener {
//        Toast.makeText(mContext, "제목 fdfdf!", Toast.LENGTH_SHORT).show()
    }

    /**
     * 연필
     * 일지 작성(WriteActivity 이동)
     */
    private val mPencilBtnClickListener : View.OnClickListener = OnClickListener {
        val intent = Intent(mContext, WriteActivity::class.java)
        (mContext as Activity).startActivity(intent)
    }

    // 지우개
    private var mEraserBtnClickListener : View.OnClickListener = OnClickListener { }


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
    fun setClose() {
        top_iv_logo.setImageResource(R.drawable.btn_close_selector)
        top_iv_logo.setOnClickListener(mCloseBtnClickListener)
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
        if (!at.hasValue(R.styleable.TopTitleView_imgLogo)) {
            top_iv_logo.visibility = View.GONE
        }

        // 상단 바 타이틀
        if (!at.hasValue(R.styleable.TopTitleView_tvTitle)) {
            top_tv_title.visibility = View.GONE
        }

        // 상단 바 닫기
        if (!at.hasValue(R.styleable.TopTitleView_btnClose)) {
            top_btn_close.visibility = View.GONE
        } else {
            top_btn_close.setOnClickListener(mCloseBtnClickListener)
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

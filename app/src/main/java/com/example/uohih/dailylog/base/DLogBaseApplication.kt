package com.example.uohih.dailylog.base

import android.app.Activity
import android.app.Application
import android.support.v4.os.HandlerCompat.postDelayed
import org.json.JSONObject
import com.example.uohih.dailylog.main.MainActivity
import android.content.Intent
import android.os.Handler
import android.support.v4.os.HandlerCompat.postDelayed


class DLogBaseApplication : Application() {


    /**
     * ------------- 날짜 세팅 -------------
     */
    fun setDateInfom(dateInfo: JSONObject) {
        Companion.dateInfo = dateInfo
    }

    fun getDateInfom(): JSONObject {
        return dateInfo
    }

    /**
     * ------------- 전체 선택 체크박스 세팅 -------------
     */
    fun setAllCheckBox(allCheckBox: Boolean) {
        Companion.allCheckBox = allCheckBox
    }

    fun getAllCheckBox(): Boolean {
        return allCheckBox
    }

    /**
     * ------------- 삭제 항목 세팅 -------------
     */
    fun setDeleteItem(deleteItem: ArrayList<String>?) {
        Companion.deleteItem.clear()
        if (deleteItem != null) {
            LogUtil.d(deleteItem.toString())
            Companion.deleteItem = deleteItem
        }
    }

    fun getDeleteItem(): ArrayList<String> {
        return deleteItem
    }


    /**
     * ------------- 캘린더 팝업 날짜 세팅 -------------
     */
    fun setSeleteDate(selecteDate:String){
        Companion.selecteDate = selecteDate
    }
    fun getSeleteDate():String{
        return selecteDate
    }


    /**
     * ------------- 월간 일지 -------------
     */
    fun setMonthly(monthly:Boolean){
        Companion.monthly = monthly
    }
    fun getMonthly():Boolean{
        return monthly
    }



    companion object {
        private var dateInfo: JSONObject = DLogBaseActivity().getToday() //설정 날짜
        private var allCheckBox = false //전체 선택
        private var deleteItem = ArrayList<String>()//삭제 항목
        private var selecteDate=DLogBaseActivity().getToday().get("yyyymmdd").toString()
        private var monthly=false
    }




}
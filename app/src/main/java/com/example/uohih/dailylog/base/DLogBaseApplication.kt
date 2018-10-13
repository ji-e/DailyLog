package com.example.uohih.dailylog.base

import android.app.Application
import org.json.JSONObject

class DLogBaseApplication : Application() {


    /**
     * 날짜 세팅
     */
    fun setDateInfom(dateInfo: JSONObject) {
        Companion.dateInfo = dateInfo
    }

    fun getDateInfom(): JSONObject {
        return Companion.dateInfo
    }

    /**
     * 전체 선택 체크박스 세팅
     */
    fun setAllCheckBox(allCheckBox: Boolean) {
        Companion.allCheckBox = allCheckBox
    }

    fun getAllCheckBox(): Boolean {
        return Companion.allCheckBox
    }

    /**
     * 삭제 항목 세팅
     */
    fun setDeleteItem(deleteItem: ArrayList<String>?) {
        Companion.deleteItem.clear()
        if (deleteItem != null) {
            LogUtil.d(deleteItem.toString())
            Companion.deleteItem = deleteItem
        }
    }

    fun getDeleteItem(): ArrayList<String> {
        return Companion.deleteItem
    }

    /**
     * 체크 항목 세팅
     */
    fun setCheckOnItem(index: String, check: String) {
        Companion.checkItem.put(index, check)
    }

    fun setCheckOffItem(index: String) {
        Companion.checkItem.remove(index)
    }

    fun getCheckItem(): JSONObject {
        return Companion.checkItem
    }


    companion object {
        private var dateInfo: JSONObject = DLogBaseActivity().getToday() //설정 날짜
        private var allCheckBox = false //전체 선택
        private var deleteItem = ArrayList<String>()//삭제 항목
        private var checkItem = JSONObject()

    }


}
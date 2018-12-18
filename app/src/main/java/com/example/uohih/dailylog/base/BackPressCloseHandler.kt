package com.example.uohih.dailylog.base

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.daily_item_recycler_view.view.*

class BackPressCloseHandler(mContext: Context){
    private var backKeyPressedTime:Long=0
    private var mContext=mContext
    lateinit var toast: Toast

    fun onBackPressed(){
        if(System.currentTimeMillis()>backKeyPressedTime+2000){
            backKeyPressedTime=System.currentTimeMillis()
            toast= Toast.makeText(mContext, "한번 더 누르시면 종료 됩니다.",Toast.LENGTH_SHORT)
            toast.show()
            return
        }

        if(System.currentTimeMillis()<=backKeyPressedTime+2000){
            (mContext as Activity).finish()
            android.os.Process.killProcess(android.os.Process.myPid())
            toast.cancel()
        }

    }

}
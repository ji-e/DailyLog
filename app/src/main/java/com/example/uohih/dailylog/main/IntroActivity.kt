package com.example.uohih.dailylog.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.example.uohih.dailylog.R
import com.example.uohih.dailylog.base.DLogBaseActivity
import com.example.uohih.dailylog.base.DLogBaseApplication
import com.example.uohih.dailylog.base.LogUtil
import com.example.uohih.dailylog.setting.PasswordCheckActivity
import kotlinx.android.synthetic.main.activity_intro.*

/**
 * 인트로
 */
class IntroActivity : DLogBaseActivity() {

    lateinit var mListener: mPermissionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        // 퍼미션 체크
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            var arry = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, arry, 0)

            setmPermissionListener(object : mPermissionListener {
                override fun onFail() {
                    finish()
                }

                override fun onSuccess() {
                    // 화면이동
                    go()
                }
            })

        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // 화면이동
            go()
        }

    }

    /**
     * 다음 화면 이동
     */
    fun go(){
        intro_activity.postDelayed({
            LogUtil.d((getPreference(passwordSetting)))
            if (getPreference(passwordSetting) != "") {
                var intent = Intent(this, PasswordCheckActivity::class.java)
                startActivityForResult(intent, passwordCheck)
            } else {
                goPage()
            }
        }, 2000)
    }

    override fun onResume() {
        super.onResume()

    }

    /**
     *  permission check start
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    mListener.onFail()
                }else{
                    mListener.onSuccess()
                }
            }
        }
    }

    interface mPermissionListener {
        fun onFail()
        fun onSuccess()
    }

    fun setmPermissionListener(listener: mPermissionListener) {
        mListener = listener
    }
    /**
     * permission check end
     */


    /**
     * 비밀번호 확인
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                passwordCheck -> {
                    goPage()
                }
            }
        } else {
            exit()
        }
    }

    /**
     *  다음 화면으로 이동
     */
    private fun goPage() {
        // 프리퍼런스에 저장된 액티비티 값 가져오기
        val result = getPreference(activitySetting)

        //2초후 저장된 액티비티로 이동
        var intent: Intent? = null
        when (result) {
            "daily" -> intent = Intent(this, DailyActivity::class.java)
            "weekly" -> intent = Intent(this, WeeklyActivity::class.java)
            "monthly" -> intent = Intent(this, MonthlyActivity::class.java)
            else -> intent = Intent(this, DailyActivity::class.java)
        }
        startActivity(intent)
        finish()

    }
}

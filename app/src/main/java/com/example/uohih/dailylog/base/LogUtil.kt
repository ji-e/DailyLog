package com.example.uohih.dailylog.base

import android.util.Log
import java.text.MessageFormat

/**
 * log
 */
object LogUtil {
    private val TAG = "MY_TAG"

    fun logMethodCalled() {
        doLog(Log.DEBUG, "called")
    }

    fun v(text: Any) {
        doLog(Log.VERBOSE, text.toString())
    }

    fun d(text: Any) {
        doLog(Log.DEBUG, text.toString())
    }

    fun i(text: Any) {
        doLog(Log.INFO, text.toString())
    }

    fun w(text: Any) {
        doLog(Log.WARN, text.toString())
    }

    fun e(text: Any) {
        doLog(Log.ERROR, text.toString())
    }


    private fun doLog(logLevel: Int, logText: String) {
        var logText = logText

        val stackTrace = Thread.currentThread()
                .stackTrace

        //take stackTrace element at index 4 because:
        //0: VMStack.getThreadStackTrace(Native Method)
        //1: java.lang.Thread.getStackTrace
        //2: LogUtil -> doLog method (this method)
        //3: LogUtil -> log method
        //4: this is the calling method!
        if (stackTrace != null && stackTrace.size > 4) {

            val element = stackTrace[4]

            val fullClassName = element.className
            val simpleClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)

            //add class and method data to logText
            logText = MessageFormat.format("T:{0} | {1} , {2}() {3}| {4}", Thread.currentThread()
                    .id, simpleClassName, element.methodName, element.lineNumber, logText)
        }

        Log.println(logLevel, TAG, logText)
    }

}
package com.shine56.nicebus.util

import android.util.Log

object LogUtil {
    private const val FLAG = 3
    fun logD(tag: String, message: String){
        if(FLAG > 1) Log.d("我的调试-$tag", message)
    }
    fun logD(message: String){
        if(FLAG > 1) Log.d("我的调试", message)
    }
    fun logE(tag: String, message: String){
        if(FLAG > 2) Log.e("异常调试-$tag", message)
    }
}
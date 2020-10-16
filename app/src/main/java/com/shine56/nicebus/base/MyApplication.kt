package com.shine56.nicebus.base

import android.app.Application
import android.content.Context
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer

class MyApplication : Application() {

    companion object{
        lateinit var context: Context
        const val baseUrl = "http://123.56.160.202:8086/"
        const val defaultLon = 113.1179210392234
        const val defaultLat = 113.1179210392234
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this)
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL)
    }
}
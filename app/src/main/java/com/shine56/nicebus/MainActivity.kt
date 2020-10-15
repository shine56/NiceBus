package com.shine56.nicebus

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng


class MainActivity : AppCompatActivity() {

    private lateinit var mMapView : MapView
    private lateinit var mBaiduMap: BaiduMap
    private  val mLocationClient by lazy { LocationClient(this) }

    // 是否是第一次定位
    private var isFirstLocate = true

    // 当前定位模式
    private val locationMode: MyLocationConfiguration.LocationMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMapView = findViewById(R.id.bmapView)
        mBaiduMap = mMapView.map


        request()
        mBaiduMap.setMyLocationEnabled(true)
        //通过LocationClientOption设置LocationClient相关参数
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps

        option.setCoorType("bd09ll") // 设置坐标类型

        option.setScanSpan(1000)

        //设置locationClientOption
        mLocationClient.setLocOption(option)

        //注册LocationListener监听器
        val myLocationListener = MyLocationListener()
        mLocationClient.registerLocationListener(myLocationListener)

        //开启地图定位图层
        mLocationClient.start()
    }



    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            if (location == null || mMapView == null){
                return
            }
            // 如果是第一次定位
            val ll = LatLng(location.latitude, location.longitude)
            if (isFirstLocate) {
                isFirstLocate = false
                //给地图设置状态
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ll))
            }

            val locData = MyLocationData.Builder()
                .accuracy(location.radius) // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.direction).latitude(location.latitude)
                .longitude(location.longitude).build()
            mBaiduMap.setMyLocationData(locData)

            // 显示当前信息

            // 显示当前信息
            val stringBuilder = StringBuilder()
            stringBuilder.append(
                """
                    
                    经度：${location.latitude}
                    """.trimIndent()
            )
            stringBuilder.append(
                """
                    
                    纬度：${location.longitude}
                    """.trimIndent()
            )
            stringBuilder.append(
                """
                    
                    状态码：${location.locType}
                    """.trimIndent()
            )
            stringBuilder.append(
                """
                    
                    国家：${location.country}
                    """.trimIndent()
            )
            stringBuilder.append(
                """
                    
                    城市：${location.city}
                    """.trimIndent()
            )
            stringBuilder.append(
                """
                    
                    区：${location.district}
                    """.trimIndent()
            )
            stringBuilder.append(
                """
                    
                    街道：${location.street}
                    """.trimIndent()
            )
            stringBuilder.append(
                """
                    
                    地址：${location.addrStr}
                    """.trimIndent()
            )

            Log.d("调试", "onReceiveLocation: ${stringBuilder.toString()}")
        }
    }

    private fun request(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }else {
            Toast.makeText(this,"您已经申请了权限!",Toast.LENGTH_SHORT).show();
        }
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause()
    }

    override fun onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy()
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理

    }
}
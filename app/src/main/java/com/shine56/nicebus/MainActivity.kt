package com.shine56.nicebus

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener
import com.baidu.mapapi.model.LatLng
import com.shine56.nicebus.model.Bus
import com.shine56.nicebus.util.PhotoUtil
import com.shine56.nicebus.util.logD
import com.shine56.nicebus.util.toast
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var vm : MainVm
    private lateinit var mMapView : MapView
    private lateinit var mBaiduMap: BaiduMap
    private  val mLocationClient by lazy { LocationClient(this) }
    private lateinit var busInfoTv: TextView
    private lateinit var busInfoDialog: Dialog

    // 是否是第一次定位
    private var isFirstLocate = true

    // 当前定位模式
    private val locationMode: MyLocationConfiguration.LocationMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm = ViewModelProvider(this)[MainVm::class.java]

        //请求权限
        request()
    }

    private fun initView(){
        //初始化地图
        initMap()
        initDialog()

        //观察数据
        initObserve()

        //刷新所有车辆
        vm.refreshData()
    }

    /**
     * 观察数据
     */
    private fun initObserve(){
        vm.busList.observe(this, Observer {
            if(it.status == 200 && it.data != null){
                "校园内找到了${it.data!!.size}辆巴士".toast()
                for(bus in it.data!!){
                    setBusImg(bus.lon.toDouble(), bus.lat.toDouble())
                }
            }else{
                "没有找到校园巴士".toast()
            }
        })

        vm.points.observe(this, Observer {
            setRoute(it)
        })
    }

    private fun initDialog(){
        busInfoDialog = Dialog(this, R.style.BottomDialog)
        //填充对话框的布局
        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog,null, false)
        busInfoDialog.setContentView(view)

        val dialogWindow = busInfoDialog.window
        //设置Dialog从窗体底部弹出
        dialogWindow!!.setGravity(Gravity.BOTTOM)
        //获得窗体的属性
        val lp = dialogWindow.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        dialogWindow.attributes = lp

        busInfoTv = view.findViewById(R.id.dialog_text)
        val cancel = view.findViewById<ImageView>(R.id.dialog_cancel)
        cancel.setOnClickListener {
            busInfoDialog.hide()
        }
    }

    /**
     * 初始化地图
     */
    private fun initMap(){
        //控件
        mMapView = findViewById(R.id.bmapView)
        mBaiduMap = mMapView.map

        //定位
        //开启地图的定位图层
        mBaiduMap.setMyLocationEnabled(true)
        //通过LocationClientOption设置LocationClient相关参数
        val option = LocationClientOption()
        // 打开gps
        option.isOpenGps = true
        // 设置坐标类型
        option.setCoorType("bd09ll")
        option.setScanSpan(1000)
        //设置locationClientOption
        mLocationClient.setLocOption(option)
        //注册LocationListener监听器
        val myLocationListener = MyLocationListener()
        mLocationClient.registerLocationListener(myLocationListener)
        //启动地图定位图层
        mLocationClient.start()

        //设置缩放比例
        val builder = MapStatus.Builder()
        builder.zoom(18.0f)
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))

        //设置地图单击事件监听

        mBaiduMap.setOnMarkerClickListener(object : BaiduMap.OnMarkerClickListener {
            //marker被点击时回调的方法
            //若响应点击事件，返回true，否则返回false
            //默认返回false
            override fun onMarkerClick(marker: Marker): Boolean {
                val bus = vm.isClickBus(marker.position.longitude, marker.position.latitude)
                bus?.let {
                    vm.getRoute(it.id)
                    showDialog(bus)
                }
                return true
            }
        })
    }

    /**
     * 展示bus信息
     * @param bus Bus
     */
    private fun showDialog(bus: Bus){
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE)
        val date = Date(bus.time)
        val time= simpleDateFormat.format(date)
        val text = "车牌号：${bus.id}\n" +
                "时间：$time\n" +
                "速度：${bus.speed}\n" +
                "核载人数：${bus.loadNumberOfPeople}\n" +
                "车上人数：${bus.numberOfPeople}\n" +
                "未带口罩人数：${bus.numberOfPeopleMasks}\n" +
                "吸烟人数：${bus.smoking}\n"
        busInfoDialog.show()
        busInfoTv.text = text
    }

    /**
     * 设置巴士图标
     * @param lon Double
     * @param lat Double
     */
    private fun setBusImg(lon: Double, lat: Double){
        //定义Maker坐标点
        val point = LatLng(lat, lon)
        //构建Marker图标
        val bitmap = BitmapDescriptorFactory.fromBitmap(
            PhotoUtil.zoomPhoto(
                BitmapDescriptorFactory.fromResource(R.drawable.bus).bitmap))

        //构建MarkerOption，用于在地图上添加Marker
        val option: OverlayOptions = MarkerOptions()
            .position(point)
            .icon(bitmap)
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option)
    }

    /**
     * 绘制路线
     * @param points ArrayList<LatLng>
     */
    private fun setRoute(points: ArrayList<LatLng>){
        //设置折线的属性
        val mOverlayOptions: OverlayOptions = PolylineOptions()
            .width(15)
            .color(resources.getColor(R.color.routeColor))
            .points(points)
        //在地图上绘制折线
        //mPloyline 折线对象
        val mPolyline = mBaiduMap.addOverlay(mOverlayOptions)
    }

    /**
     * 定位监听
     */
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
            initView()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1){
            if(grantResults[0] == PERMISSION_GRANTED){
                initView()
            }else{
                "您拒绝了授权。".toast()
            }
        }
    }
}
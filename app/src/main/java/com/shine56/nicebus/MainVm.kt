package com.shine56.nicebus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.baidu.mapapi.model.LatLng
import com.shine56.nicebus.base.MyViewModel
import com.shine56.nicebus.model.Bus
import com.shine56.nicebus.model.BusRoute
import com.shine56.nicebus.network.BusNetWork
import com.shine56.nicebus.util.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.math.abs

class MainVm: MyViewModel() {

    val points = MutableLiveData<ArrayList<LatLng>>()

    val busList = refreshLiveData.switchMap { liveData(viewModelScope.coroutineContext+ Dispatchers.IO) {
        val result = BusNetWork.getAllBus()
        emit(result)
    }}

    fun getRoute(id: String){
        viewModelScope.async(Dispatchers.IO) {
            val busRoute = BusNetWork.getBusRoute(id).data
            val pointList = arrayListOf<LatLng>()
            for (point in busRoute){
                pointList.add(LatLng(point.lat.toDouble(), point.lon.toDouble()))
            }
            points.postValue(pointList)
        }
    }

    fun isClickBus(lon: Double, lat: Double): Bus?{
        busList.value?.data?.let {
            for (bus in it){
                "lon = $lon bus.lon.toDouble() = ${bus.lon.toDouble()}".logD()
                "lat = $lat bus.lat.toDouble() = ${bus.lat.toDouble()}".logD()
                if (lon == bus.lon.toDouble() && lat == bus.lat.toDouble()){
                    return bus
                }
            }
        }
        return null
    }
}
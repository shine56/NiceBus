package com.shine56.nicebus

import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.shine56.nicebus.base.MyViewModel
import com.shine56.nicebus.model.Bus
import com.shine56.nicebus.network.BusNetWork
import com.shine56.nicebus.util.logD
import kotlinx.coroutines.Dispatchers
import kotlin.math.abs

class MainVm: MyViewModel() {

    val busList = refreshLiveData.switchMap { liveData(viewModelScope.coroutineContext+ Dispatchers.IO) {
        val result = BusNetWork.getAllBus()
        emit(result)
    }}

    fun isClickBus(lon: Double, lat: Double): Bus?{
        busList.value?.data?.let {
            for (bus in it){
                if (abs((bus.lon.toDouble()-lon)) <0.00020 && abs((bus.lat.toDouble()-lat)) <0.00020){
                    return bus
                }
            }
        }
        return null
    }
}
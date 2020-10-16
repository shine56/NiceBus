package com.shine56.nicebus.network

import com.shine56.nicebus.model.AllBus
import com.shine56.nicebus.model.BusRoute
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GetBusData {
    //
    @GET("/home/home/getdata")
    fun getAllBus(): Call<AllBus>

    @GET("/home/home/getroute/{busId}")
    fun getBusRoute(@Path("busId")busId: String): Call<BusRoute>
}
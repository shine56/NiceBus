package com.shine56.nicebus.network

import com.shine56.nicebus.model.AllBus
import retrofit2.Call
import retrofit2.http.GET

interface GetBusData {
    //
    @GET("/home/home/getdata")
    fun getAllBus(): Call<AllBus>
}
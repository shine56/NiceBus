package com.shine56.nicebus.network

import retrofit2.await

object BusNetWork {
    private val service = RetrofitCreator.create(GetBusData::class.java)
    suspend fun getAllBus()= service.getAllBus().await()
}
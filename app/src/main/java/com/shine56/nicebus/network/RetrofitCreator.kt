package com.shine56.nicebus.network

import com.shine56.nicebus.base.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitCreator {
    fun<T> create(serviceClass: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(MyApplication.baseUrl)
            .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
            .build()
        return retrofit.create(serviceClass)
    }
}
package com.shine56.nicebus.model

data class AllBus(
    var status: Int,
    var data: List<Bus>?
)

data class Bus(
    var id: String,
    var lon: String,
    var lat: String,
    var numberOfPeople: Int,
    var loadNumberOfPeople: Int,
    var numberOfPeopleMasks: Int,
    var smoking: Int,
    var speed: String,
    var time: Long
)



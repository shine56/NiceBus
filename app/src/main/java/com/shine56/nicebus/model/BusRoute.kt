package com.shine56.nicebus.model

data class BusRoute(
    var status: Int,
    var data: List<Route>
)

data class Route(
    var lon: String,
    var lat: String,
    var time: Long
)
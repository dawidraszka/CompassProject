package pl.dawidraszka.compassproject.model

import android.location.Location

data class SimplePosition(var latitude: Double, var longitude: Double) {

    fun toLocation(): Location = Location("").apply {
        latitude = this@SimplePosition.latitude
        longitude = this@SimplePosition.longitude
    }

    fun bearingTo(destination: SimplePosition) =
        toLocation().bearingTo(destination.toLocation())
}

package pl.dawidraszka.compassproject.model.data

import android.location.Location

data class SimplePosition(var latitude: Double, var longitude: Double) {

    fun bearingTo(destination: SimplePosition) =
        toLocation().bearingTo(destination.toLocation())

    private fun toLocation(): Location = Location("").apply {
        latitude = this@SimplePosition.latitude
        longitude = this@SimplePosition.longitude
    }
}

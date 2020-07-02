package pl.dawidraszka.compassproject.model.repositories

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import pl.dawidraszka.compassproject.model.SimplePosition
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PositionRepository @Inject constructor() {
    val currentPosition = MutableLiveData<SimplePosition>()
    val destinationBearing = MutableLiveData<Float>()
    val destination = MutableLiveData<SimplePosition>()

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission") //TODO DEAL WITH PERMISSION
    fun startPositionUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopPositionUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun setDestination(destination: SimplePosition) {
        this.destination.value = destination
        updateBearing()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            with(locationResult.lastLocation) {
                currentPosition.value = SimplePosition(latitude, longitude)
                //currentPosition.postValue(SimplePosition(latitude, longitude))
                updateBearing()
            }
        }
    }

    private fun updateBearing() {
        val currentPosition = currentPosition.value
        val destination = destination.value

        if (currentPosition != null && destination != null)
            destinationBearing.value = currentPosition.bearingTo(destination)
            //destinationBearing.postValue(currentPosition.bearingTo(destination))
    }
}
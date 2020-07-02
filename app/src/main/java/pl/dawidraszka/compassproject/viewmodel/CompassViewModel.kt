package pl.dawidraszka.compassproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import pl.dawidraszka.compassproject.model.Direction
import pl.dawidraszka.compassproject.model.SimplePosition
import pl.dawidraszka.compassproject.model.repositories.DirectionRepository
import pl.dawidraszka.compassproject.model.repositories.PositionRepository
import javax.inject.Inject

class CompassViewModel @Inject constructor(
    private val directionRepository: DirectionRepository,
    private val positionRepository: PositionRepository
) :
    ViewModel() {

    fun getDirection(): LiveData<Direction> = directionRepository.currentDirection
    fun getSensorAccuracy(): LiveData<Int> = directionRepository.sensorAccuracy
    fun getPosition(): LiveData<SimplePosition> = positionRepository.currentPosition
    fun getDestinationBearing(): LiveData<Float> = positionRepository.destinationBearing
    fun getDestination(): LiveData<SimplePosition> = positionRepository.destination

    fun onResume() {
        directionRepository.startDirectionUpdates()
        positionRepository.startPositionUpdates()
    }

    fun onPause() {
        directionRepository.stopDirectionUpdates()
        positionRepository.stopPositionUpdates()
    }

    fun setDestination(destination: SimplePosition) {
        positionRepository.setDestination(destination)
    }

/*    private var needleDegree = 0f

    private var destination: Location? = null



    fun setDestination(destination: SimplePosition) {
        this.destination = Location("")
        this.destination?.longitude = destination.longitude.toDouble()
        this.destination?.latitude = destination.latitude.toDouble()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
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

    private fun updateCurrentPosition(currentPosition: SimplePosition) {
        compassView.updateCurrentPosition(currentPosition)
    }*/
}
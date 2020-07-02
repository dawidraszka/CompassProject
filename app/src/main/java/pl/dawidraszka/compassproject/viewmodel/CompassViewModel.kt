package pl.dawidraszka.compassproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import pl.dawidraszka.compassproject.model.data.Direction
import pl.dawidraszka.compassproject.model.data.SimplePosition
import pl.dawidraszka.compassproject.model.repositories.DirectionRepository
import pl.dawidraszka.compassproject.model.repositories.PositionRepository
import javax.inject.Inject

class CompassViewModel @Inject constructor(
    private val directionRepository: DirectionRepository,
    private val positionRepository: PositionRepository
) :
    ViewModel() {

    var locationPermission = false

    fun getDirection(): LiveData<Direction> = directionRepository.currentDirection
    fun getSensorAccuracy(): LiveData<Int> = directionRepository.sensorAccuracy
    fun getPosition(): LiveData<SimplePosition> = positionRepository.currentPosition
    fun getDestinationBearing(): LiveData<Float> = positionRepository.destinationBearing
    fun getDestination(): LiveData<SimplePosition> = positionRepository.destination

    fun onResume() {
        directionRepository.startDirectionUpdates()
        if (locationPermission)
            positionRepository.startPositionUpdates()
    }

    fun onPause() {
        directionRepository.stopDirectionUpdates()
        if (locationPermission)
            positionRepository.stopPositionUpdates()
    }

    fun setDestination(destination: SimplePosition) {
        positionRepository.setDestination(destination)
    }
}
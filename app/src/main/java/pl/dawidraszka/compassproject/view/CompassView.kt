package pl.dawidraszka.compassproject.view

import pl.dawidraszka.compassproject.model.Direction

interface CompassView {
    fun rotateCompassNeedle(needleDegree: Float, rotation: Float)
    fun updateDirection(direction: Direction)
}
package pl.dawidraszka.compassproject.presenter

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import pl.dawidraszka.compassproject.model.Direction
import pl.dawidraszka.compassproject.view.CompassView

class CompassPresenter(val compassView: CompassView, val sensorManager: SensorManager) :
    SensorEventListener {

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var needleDegree = 0f

    fun onResume() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun onPause() {
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO ADD ACCURACY ICON
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }
        updateOrientationAngles()
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val azimuth = orientationAngles[0]
        val degree = when {
            azimuth == 0f -> 0f
            azimuth > 0f -> (azimuth * (180 / Math.PI)).toFloat()
            else -> (360 + azimuth * (180 / Math.PI)).toFloat()
        }

        compassView.updateDirection(Direction(degree))

        if (needleDegree > degree + 180) {
            needleDegree -= 360
        }
        if (degree > needleDegree + 180) {
            needleDegree += 360
        }

        compassView.rotateCompassNeedle(needleDegree, degree)

        needleDegree = degree
    }

}
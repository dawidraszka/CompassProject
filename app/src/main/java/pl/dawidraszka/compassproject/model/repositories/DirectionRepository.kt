package pl.dawidraszka.compassproject.model.repositories

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import pl.dawidraszka.compassproject.Chain
import pl.dawidraszka.compassproject.lowPassFilter
import pl.dawidraszka.compassproject.model.Direction
import pl.dawidraszka.compassproject.model.SimplePosition
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DirectionRepository @Inject constructor() : SensorEventListener {
    val currentDirection = MutableLiveData<Direction>()

    @Inject
    lateinit var sensorManager: SensorManager

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val chain = Chain(20)

    fun startDirectionUpdates() {
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

    fun stopDirectionUpdates() = sensorManager.unregisterListener(this)


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO ADD ACCURACY ICON
    }

    override fun onSensorChanged(evt: SensorEvent) {
        if (evt.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(lowPassFilter(evt.values, accelerometerReading), 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (evt.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(lowPassFilter(evt.values, magnetometerReading), 0, magnetometerReading, 0, magnetometerReading.size)
        }
        updateOrientationAngles()
    }

    private fun updateOrientationAngles() {
        if (
            SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
            )
        ) {
            val azimuth = (Math.toDegrees(
                SensorManager.getOrientation(
                    rotationMatrix,
                    orientationAngles
                )[0].toDouble()
            ) + 360) % 360

            SensorManager.getOrientation(
                rotationMatrix,
                orientationAngles)

            chain.addElement(azimuth.toInt())

            currentDirection.postValue(Direction(chain.average()))
        }


        /*  val degree = when {
              azimuth == 0f -> 0f
              azimuth > 0f -> (azimuth * (180 / Math.PI)).toFloat()
              else -> (360 + azimuth * (180 / Math.PI)).toFloat()
          }*/


        /*  compassView.updateDirection(Direction(degree))

          if (needleDegree > degree + 180) {
              needleDegree -= 360
          }
          if (degree > needleDegree + 180) {
              needleDegree += 360
          }

          compassView.rotateCompassNeedle(needleDegree, degree)
          needleDegree = degree*/
    }

}
package pl.dawidraszka.compassproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_compass.*


const val PERMISSION_REQUEST_LOCATION = 0

class CompassActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
    SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var needleDegree = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        set_destination_button.setOnClickListener {
            updateOrientationAngles()
            if (checkSelfPermissionCompat(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // TODO ADD SETTING DESTINATION
            } else {
                requestLocationPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO ADD SETTING DESTINATION
            } else {
                Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(
                set_destination_button,
                R.string.location_permission_alert_message,
                Snackbar.LENGTH_INDEFINITE
            ).setAction(
                R.string.ok
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_LOCATION
                )
            }.show()

        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
        }
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //TODO ADD ACCURACY ICON
    }

    override fun onResume() {
        super.onResume()
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

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }
        updateOrientationAngles()
    }

    fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )


        SensorManager.getOrientation(rotationMatrix, orientationAngles)


        val azimuth = orientationAngles[0]
        val degree = when {
            azimuth == 0f -> 0
            azimuth > 0f -> (azimuth * (180 / Math.PI)).toInt()
            else -> 360 + (azimuth * (180 / Math.PI)).toInt()
        }

        val directionLetter = when (degree) {
            in 23..67 -> "NE"
            in 68..112 -> "E"
            in 113..157 -> "SE"
            in 158..202 -> "S"
            in 203..247 -> "SW"
            in 248..292 -> "W"
            in 293..337 -> "NW"
            else -> "N"
        }

        direction_tv.text = "$degree° $directionLetter"


        if(needleDegree > degree + 180){
            needleDegree -= 360
        }
        if(degree > needleDegree + 180){
            needleDegree += 360
        }

        val ra = RotateAnimation(
            needleDegree,
            degree.toFloat(),
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        ra.duration = 200
        ra.fillAfter = true

        compass_needle_iv.startAnimation(ra)

        needleDegree = degree.toFloat()
    }
}

package pl.dawidraszka.compassproject.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_compass.*
import pl.dawidraszka.compassproject.R
import pl.dawidraszka.compassproject.di.CompassApplication
import pl.dawidraszka.compassproject.model.data.Direction
import pl.dawidraszka.compassproject.model.data.SimplePosition
import pl.dawidraszka.compassproject.viewmodel.CompassViewModel
import javax.inject.Inject
import kotlin.math.abs

const val PERMISSION_REQUEST_LOCATION = 0

class CompassActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
    PositionDialogFragment.PositionDialogListener {

    @Inject
    lateinit var compassViewModel: CompassViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        (application as CompassApplication).appComponent.inject(this)

        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) removeLocationPermissionButton() else {
            provide_location_permission.setOnClickListener { requestLocationPermission() }
        }

        set_destination_button.setOnClickListener {
            if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                PositionDialogFragment().show(
                    supportFragmentManager,
                    "PositionDialogFragment"
                )
            } else {
                Snackbar.make(
                    provide_location_permission,
                    R.string.location_permission_alert_message,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(
                    R.string.ok
                ) {
                    requestPermissionsCompat(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSION_REQUEST_LOCATION
                    )
                }.show()
            }
        }

        compassViewModel.getDirection().observe(this, Observer {
            updateDirection(it)
            rotateCompassNeedle(it.angle)
        })

        compassViewModel.getSensorAccuracy().observe(this, Observer {
            updateSensorAccuracy(it)
        })

        compassViewModel.getPosition().observe(this, Observer {
            updateCurrentPosition(it)
        })

        compassViewModel.getDestinationBearing().observe(this, Observer {
            if (it != null) {
                showBearingIndicator()
                updateBearingIndicator(it)
            }
        })

        compassViewModel.getDestination().observe(this, Observer {
            if (it != null) {
                updateDestination(it)
            }
        })
    }

    private fun updateSensorAccuracy(sensorAccuracy: Int) {
        when (sensorAccuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> gps_signal_indicator_iv.colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(this, R.color.sensor_high_accuracy),
                    PorterDuff.Mode.MULTIPLY
                )
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> gps_signal_indicator_iv.colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(this, R.color.sensor_medium_accuracy),
                    PorterDuff.Mode.MULTIPLY
                )
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> gps_signal_indicator_iv.colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(this, R.color.sensor_low_accuracy),
                    PorterDuff.Mode.MULTIPLY
                )
        }
    }

    override fun onResume() {
        super.onResume()
        compassViewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        compassViewModel.onPause()
    }

    private fun updateDirection(direction: Direction) {
        direction_tv.text = direction.toString()
    }

    private fun rotateCompassNeedle(rotation: Int) {
/*        if(abs(compass_needle_iv.rotation - rotation) > 180){
            compass_needle_iv.rotation = rotation.toFloat() + 360
        } else {
            compass_needle_iv.rotation = rotation.toFloat()
        }*/

        compass_needle_iv?.animation?.cancel()

        if (abs(compass_needle_iv.rotation - rotation) > 180)
            compass_needle_iv.animate().rotationBy(rotation + 360 - compass_needle_iv.rotation).setDuration(100).start()
        else
            compass_needle_iv.animate().rotationBy(rotation - compass_needle_iv.rotation).setDuration(100).start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            val message =
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    removeLocationPermissionButton()
                    R.string.location_permission_granted
                } else R.string.location_permission_denied

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeLocationPermissionButton() {
        compassViewModel.locationPermission = true
        provide_location_permission.visibility = View.GONE
    }

    private fun requestLocationPermission() {
        requestPermissionsCompat(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_LOCATION
        )
    }

    private fun updateCurrentPosition(currentPosition: SimplePosition) {
        current_latitude_tv.text = currentPosition.latitude.toString()
        current_longitude_tv.text = currentPosition.longitude.toString()
    }

    private fun updateDestination(destination: SimplePosition) {
        destination_latitude_tv.text = destination.latitude.toString()
        destination_longitude_tv.text = destination.longitude.toString()
    }

    override fun onDialogPositiveClick(destination: SimplePosition) {
        compassViewModel.setDestination(destination)
    }

    private fun showBearingIndicator() {
        compass_indicator_iv.visibility = View.VISIBLE
    }

    private fun updateBearingIndicator(angle: Float) {
        compass_indicator_iv.rotation = angle
    }
}

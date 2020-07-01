package pl.dawidraszka.compassproject.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_compass.*
import pl.dawidraszka.compassproject.R
import pl.dawidraszka.compassproject.di.CompassApplication
import pl.dawidraszka.compassproject.model.Direction
import pl.dawidraszka.compassproject.model.SimplePosition
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

        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) removeLocationPermissionButton() else {
            provide_location_permission.setOnClickListener { requestLocationPermission() }
        }

        set_destination_button.setOnClickListener {
            PositionDialogFragment().show(
                supportFragmentManager,
                "PositionDialogFragment"
            )
        }
        (application as CompassApplication).appComponent.inject(this)

        compassViewModel.getDirection().observe(this, Observer {
            updateDirection(it)
            rotateCompassNeedle(it.angle)
        })

        compassViewModel.getPosition().observe(this, Observer {
            updateCurrentPosition(it)
        })
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
        ///compass_needle_iv.rotation = rotation.toFloat()

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
        provide_location_permission.visibility = View.GONE
    }

    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.ACCESS_FINE_LOCATION)) {
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

        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
        }
    }

    private fun updateCurrentPosition(currentPosition: SimplePosition) {
        current_latitude_tv.text = currentPosition.latitude.toString()
        current_longitude_tv.text = currentPosition.longitude.toString()
    }

    override fun onDialogPositiveClick(destination: SimplePosition) {
        destination_latitude_tv.text = destination.latitude.toString()
        destination_longitude_tv.text = destination.longitude.toString()
        compassViewModel.setDestination(destination)

        compassViewModel.getDirectionBearing().observe(this, Observer {
            if(it != null){
                showBearingIndicator()
                updateBearingIndicator(it)
            }
        })
    }

    private fun showBearingIndicator() {
        compass_indicator_iv.visibility = View.VISIBLE
    }


    private fun updateBearingIndicator(angle: Float) {
        compass_indicator_iv?.animation?.cancel()

        if (abs(compass_indicator_iv.rotation - angle) > 180)
            compass_indicator_iv.animate().rotationBy(angle + 360 - compass_indicator_iv.rotation).setDuration(100).start()
        else
            compass_indicator_iv.animate().rotationBy(angle - compass_indicator_iv.rotation).setDuration(100).start()
    }
}

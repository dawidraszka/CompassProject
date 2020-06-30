package pl.dawidraszka.compassproject.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_compass.*

import pl.dawidraszka.compassproject.R
import pl.dawidraszka.compassproject.model.Direction
import pl.dawidraszka.compassproject.model.SimplePosition
import pl.dawidraszka.compassproject.presenter.CompassPresenter

const val PERMISSION_REQUEST_LOCATION = 0

class CompassActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
    CompassView, PositionDialogFragment.PositionDialogListener {

    private lateinit var presenter: CompassPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        presenter =
            CompassPresenter(this, getSystemService(Context.SENSOR_SERVICE) as SensorManager)

        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) removeLocationPermissionButton() else {
            provide_location_permission.setOnClickListener { requestLocationPermission() }
        }

        set_destination_button.setOnClickListener { PositionDialogFragment().show(supportFragmentManager, "PositionDialogFragment") }
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
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(
                provide_location_permission,
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


    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun rotateCompassNeedle(needleDegree: Float, rotation: Float) {
        val rotateAnimation = RotateAnimation(
            needleDegree,
            rotation,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        rotateAnimation.duration = 200
        rotateAnimation.fillAfter = true

        compass_needle_iv.startAnimation(rotateAnimation)
    }

    override fun updateDirection(direction: Direction) {
        direction_tv.text = direction.toString()
    }

    override fun onDialogPositiveClick(destination: SimplePosition) {
        destination_latitude_tv.text = destination.latitude.toString()
        destination_longitude_tv.text = destination.longitude.toString()
    }
}

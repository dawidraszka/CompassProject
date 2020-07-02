package pl.dawidraszka.compassproject.di

import android.app.Application
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val application: Application) {

    @Singleton
    @Provides
    fun provideApplicationContext() = application

    @Singleton
    @Provides
    fun provideSensorManager() = application.getSystemService(SENSOR_SERVICE) as SensorManager

    @Singleton
    @Provides
    fun provideLocationClient(): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
}
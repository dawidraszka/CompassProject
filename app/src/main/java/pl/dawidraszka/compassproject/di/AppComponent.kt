package pl.dawidraszka.compassproject.di

import dagger.Component
import pl.dawidraszka.compassproject.model.repositories.DirectionRepository
import pl.dawidraszka.compassproject.model.repositories.PositionRepository
import pl.dawidraszka.compassproject.view.CompassActivity
import pl.dawidraszka.compassproject.viewmodel.CompassViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun directionRepository(): DirectionRepository
    fun locationRepository(): PositionRepository
    fun compassViewModel(): CompassViewModel
    fun inject(compassActivity: CompassActivity)
}
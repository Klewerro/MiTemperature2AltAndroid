package com.klewerro.temperatureSensor.di

import android.content.Context
import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import com.klewerro.temperatureSensor.NordicBleThermometerRepository
import com.klewerro.temperatureSensor.NordicThermometerDevicesBleScanner
import com.klewerro.temperatureSensor.contracts.ThermometerDevicesBleScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TemperatureSensorModule {

    @Provides
    @Singleton
    fun provideThermometerRepository(
        thermometerDevicesBleScanner: ThermometerDevicesBleScanner
    ): ThermometerRepository = NordicBleThermometerRepository(thermometerDevicesBleScanner)

    @Provides
    @Singleton
    fun provideThermometerDevicesBleScanner(
        @ApplicationContext context: Context
    ): ThermometerDevicesBleScanner {
        return NordicThermometerDevicesBleScanner(context)
    }
}

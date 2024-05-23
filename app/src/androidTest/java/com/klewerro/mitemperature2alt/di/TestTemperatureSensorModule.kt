package com.klewerro.mitemperature2alt.di

import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerDevicesBleScanner
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import com.klewerro.mitemperature2alt.temperatureSensor.contracts.ThermometerDevicesBleScanner
import com.klewerro.mitemperature2alt.temperatureSensor.di.TemperatureSensorModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [TemperatureSensorModule::class]
)
object TestTemperatureSensorModule {

    @Provides
    @Singleton
    fun provideThermometerRepository(): ThermometerRepository = FakeThermometerRepository()

    @Provides
    @Singleton
    fun provideThermometerDevicesBleScanner(): ThermometerDevicesBleScanner =
        FakeThermometerDevicesBleScanner()
}

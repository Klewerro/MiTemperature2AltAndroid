package com.klewerro.mitemperaturenospyware.di

import android.content.Context
import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import com.klewerro.temperatureSensor.NordicBleThermometerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideThermometerRepository(@ApplicationContext appContext: Context): ThermometerRepository =
        NordicBleThermometerRepository(appContext)
}

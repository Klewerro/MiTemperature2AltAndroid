package com.klewerro.mitemperature2alt.di

import android.app.Application
import androidx.room.Room
import com.klewerro.mitemperature2alt.domain.repository.HourlyRecordRepository
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import com.klewerro.mitemperature2alt.persistence.RoomHourRecordRepository
import com.klewerro.mitemperature2alt.persistence.RoomThermometerRepository
import com.klewerro.mitemperature2alt.persistence.ThermometerDatabase
import com.klewerro.mitemperature2alt.persistence.di.PersistenceModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PersistenceModule::class]
)
object TestPersistenceModule {

    @Provides
    @Singleton
    fun provideThermometerDatabase(app: Application): ThermometerDatabase =
        Room.inMemoryDatabaseBuilder(
            app,
            ThermometerDatabase::class.java
        ).build()

    @Provides
    @Singleton
    fun providePersistenceRepository(
        thermometerDatabase: ThermometerDatabase
    ): PersistenceRepository = RoomThermometerRepository(thermometerDatabase.thermometerDao)

    @Provides
    @Singleton
    fun provideHourRecordRepository(
        thermometerDatabase: ThermometerDatabase
    ): HourlyRecordRepository = RoomHourRecordRepository(thermometerDatabase.hourRecordDao)
}

package com.klewerro.mitemperature2alt.persistence.di

import android.app.Application
import androidx.room.Room
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import com.klewerro.mitemperature2alt.persistence.RoomThermometerRepository
import com.klewerro.mitemperature2alt.persistence.ThermometerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideThermometerDatabase(app: Application): ThermometerDatabase {
        return Room.databaseBuilder(
            app,
            ThermometerDatabase::class.java,
            "thermometer_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePersistenceRepository(
        thermometerDatabase: ThermometerDatabase
    ): PersistenceRepository {
        return RoomThermometerRepository(thermometerDatabase.thermometerDao)
    }
}

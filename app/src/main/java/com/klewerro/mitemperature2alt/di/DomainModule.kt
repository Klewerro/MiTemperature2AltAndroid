package com.klewerro.mitemperature2alt.di

import com.klewerro.mitemperature2alt.domain.repository.HourlyRecordRepository
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import com.klewerro.mitemperature2alt.domain.usecase.GetHourlyResultsUseCase
import com.klewerro.mitemperature2alt.domain.usecase.ScanAndConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.ThermometerListUseCase
import com.klewerro.mitemperature2alt.domain.util.DispatcherProvider
import com.klewerro.mitemperature2alt.domain.util.StandardDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    @ViewModelScoped
    fun provideThermometerListUseCase(
        persistenceRepository: PersistenceRepository,
        thermometerRepository: ThermometerRepository
    ) = ThermometerListUseCase(persistenceRepository, thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideScanAndConnectToDeviceUseCase(thermometerRepository: ThermometerRepository) =
        ScanAndConnectToDeviceUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideGetHourlyResultsUseCase(
        thermometerRepository: ThermometerRepository,
        hourlyRecordRepository: HourlyRecordRepository
    ) = GetHourlyResultsUseCase(thermometerRepository, hourlyRecordRepository)
}

@Module
@InstallIn(SingletonComponent::class)
object DomainModuleSingleton {
    @Provides
    @Singleton
    fun provideDispatchers(): DispatcherProvider = StandardDispatchers()
}

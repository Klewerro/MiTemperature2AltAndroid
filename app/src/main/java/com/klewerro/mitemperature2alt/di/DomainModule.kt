package com.klewerro.mitemperature2alt.di

import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.ConnectedDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.SubscribeToCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.persistence.SavedThermometersUseCase
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
    fun provideConnectedDevicesUseCase(thermometerRepository: ThermometerRepository) =
        ConnectedDevicesUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideReadCurrentThermometerStatusUseCase(thermometerRepository: ThermometerRepository) =
        ReadCurrentThermometerStatusUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideSubscribeToCurrentThermometerStatusUseCase(
        thermometerRepository: ThermometerRepository
    ) = SubscribeToCurrentThermometerStatusUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideSavedThermometersUseCase(persistenceRepository: PersistenceRepository) =
        SavedThermometersUseCase(persistenceRepository)
}

@Module
@InstallIn(SingletonComponent::class)
object DomainModuleSingleton {
    @Provides
    @Singleton
    fun provideDispatchers(): DispatcherProvider = StandardDispatchers()
}

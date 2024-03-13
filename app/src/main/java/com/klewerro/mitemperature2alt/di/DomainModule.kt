package com.klewerro.mitemperature2alt.di

import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectedDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.SubscribeToCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.IsScanningForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.ScanForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.SearchedDevicesUseCase
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
    fun provideIsScanningForDevicesUseCase(thermometerRepository: ThermometerRepository) =
        IsScanningForDevicesUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideScanForDevicesUseCase(thermometerRepository: ThermometerRepository) =
        ScanForDevicesUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideSearchedDevicesUseCase(thermometerRepository: ThermometerRepository) =
        SearchedDevicesUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideConnectedDevicesUseCase(thermometerRepository: ThermometerRepository) =
        ConnectedDevicesUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideConnectToDeviceUseCase(thermometerRepository: ThermometerRepository) =
        ConnectToDeviceUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideReadCurrentThermometerStatusUseCase(thermometerRepository: ThermometerRepository) =
        ReadCurrentThermometerStatusUseCase(thermometerRepository)

    @Provides
    @ViewModelScoped
    fun provideSubscribeToCurrentThermometerStatusUseCase(
        thermometerRepository: ThermometerRepository
    ) = SubscribeToCurrentThermometerStatusUseCase(thermometerRepository)
}

@Module
@InstallIn(SingletonComponent::class)
object DomainModuleSingleton {
    @Provides
    @Singleton
    fun provideDispatchers(): DispatcherProvider = StandardDispatchers()
}

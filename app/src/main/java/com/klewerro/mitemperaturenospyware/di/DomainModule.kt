package com.klewerro.mitemperaturenospyware.di

import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.connect.ConnectToDeviceUseCase
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.connect.ConnectedDevicesUseCase
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.operations.SubscribeToCurrentThermometerStatusUseCase
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.scan.IsScanningForDevicesUseCase
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.scan.ScanForDevicesUseCase
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.scan.SearchedDevicesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

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
    fun provideSubscribeToCurrentThermometerStatusUseCase(thermometerRepository: ThermometerRepository) =
        SubscribeToCurrentThermometerStatusUseCase(thermometerRepository)
}

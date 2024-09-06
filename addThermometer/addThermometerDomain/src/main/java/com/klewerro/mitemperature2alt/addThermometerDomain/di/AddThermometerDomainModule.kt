package com.klewerro.mitemperature2alt.addThermometerDomain.di

import com.klewerro.mitemperature2alt.addThermometerDomain.usecase.ConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.addThermometerDomain.usecase.SearchedDevicesUseCase
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AddThermometerDomainModule {

    @Provides
    @ViewModelScoped
    fun provideSearchedDevicesUseCase(
        thermometerRepository: ThermometerRepository,
        persistenceRepository: PersistenceRepository
    ) = SearchedDevicesUseCase(
        thermometerRepository,
        persistenceRepository
    )

    @Provides
    @ViewModelScoped
    fun provideConnectToDeviceUseCase(thermometerRepository: ThermometerRepository) =
        ConnectToDeviceUseCase(thermometerRepository)
}

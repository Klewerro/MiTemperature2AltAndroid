package com.klewerro.mitemperature2alt.thermometerDetails.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.coreUi.UiConstants
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ThermometerDetailsViewModel @Inject constructor(
    savedState: SavedStateHandle,
    persistenceRepository: PersistenceRepository
) : ViewModel() {

    private val thermometerFlow = persistenceRepository.savedThermometers.map {
        it.first { thermometer ->
            val address = savedState.get<String>(UiConstants.NAV_PARAM_ADDRESS)!!
            thermometer.address == address
        }
    }

    private val _state = MutableStateFlow(ThermometerDetailsState(null))
    val state = combine(
        _state,
        thermometerFlow
    ) { stateValue, thermometerValue ->
        stateValue.copy(
            thermometer = thermometerValue
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ThermometerDetailsState(null))
}

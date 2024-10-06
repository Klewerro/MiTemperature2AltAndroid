package com.klewerro.mitemperature2alt.thermometerDetails.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.coreUi.UiConstants
import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.repository.HourlyRecordRepository
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ThermometerDetailsViewModel @Inject constructor(
    savedState: SavedStateHandle,
    persistenceRepository: PersistenceRepository,
    hourlyRecordRepository: HourlyRecordRepository
) : ViewModel() {

    private val thermometerAddress = savedState.get<String>(UiConstants.NAV_PARAM_ADDRESS)!!
    private val thermometerFlow = persistenceRepository.observeThermometer(thermometerAddress)

    private val hourlyRecords = MutableStateFlow<List<HourlyRecord>>(emptyList())

    private val _state = MutableStateFlow(ThermometerDetailsState(null))
    val state = combine(
        _state,
        thermometerFlow,
        hourlyRecords
    ) { stateValue, thermometerValue, hourlyRecordsValue ->
        stateValue.copy(
            thermometer = thermometerValue,
            hourlyRecords = hourlyRecordsValue
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ThermometerDetailsState(null))

    init {
        hourlyRecordRepository.observeAllThermometerRecords(thermometerAddress)
            .onEach { newList ->
                hourlyRecords.update {
                    newList.reversed()
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}

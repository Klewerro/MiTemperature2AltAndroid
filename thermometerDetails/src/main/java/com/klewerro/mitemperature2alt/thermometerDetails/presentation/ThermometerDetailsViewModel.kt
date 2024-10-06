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
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class ThermometerDetailsViewModel @Inject constructor(
    savedState: SavedStateHandle,
    persistenceRepository: PersistenceRepository,
    private val hourlyRecordRepository: HourlyRecordRepository
) : ViewModel() {

    private val thermometerAddress = savedState.get<String>(UiConstants.NAV_PARAM_ADDRESS)!!
    private val thermometerFlow = persistenceRepository
        .observeThermometer(thermometerAddress)

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

    private var refreshRecordsJob: Job? = null

    init {

        viewModelScope.launch {
            thermometerFlow.filterNotNull().first()
            refreshRecords()
        }
    }

    fun onEvent(event: ThermometerDetailsEvent) {
        when (event) {
            ThermometerDetailsEvent.OnNextDaySelected -> {
                val currentDate = Clock.System.now().toLocalDateTime(
                    TimeZone.currentSystemDefault()
                ).date
                val stateDate = state.value.selectedDate

                if (stateDate.plus(1, DateTimeUnit.DAY) > currentDate) {
                    return
                }

                _state.update {
                    it.copy(
                        selectedDate = it.selectedDate.plus(1, DateTimeUnit.DAY)
                    )
                }
                refreshRecords()
            }
            ThermometerDetailsEvent.OnPreviousDaySelected -> {
                _state.update {
                    it.copy(
                        selectedDate = it.selectedDate.minus(1, DateTimeUnit.DAY)
                    )
                }
                refreshRecords()
            }
        }
    }

    private fun refreshRecords() {
        refreshRecordsJob?.cancel("Cancelled before refresh.")
        val stateValue = state.value
        refreshRecordsJob = hourlyRecordRepository.getThermometerRange(
            stateValue.thermometer!!.address,
            stateValue.selectedDate,
            stateValue.selectedDate
        )
            .onEach { newList ->
                hourlyRecords.update {
                    newList
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}

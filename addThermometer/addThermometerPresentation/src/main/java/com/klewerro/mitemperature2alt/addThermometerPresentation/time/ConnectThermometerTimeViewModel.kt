package com.klewerro.mitemperature2alt.addThermometerPresentation.time

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.coreUi.UiConstants
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.atDate
import kotlinx.datetime.atTime

@HiltViewModel
class ConnectThermometerTimeViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val thermometerRepository: ThermometerRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ConnectThermometerTimeState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val deviceAddress = savedState.get<String>(UiConstants.NAV_PARAM_ADDRESS)!!
            val deviceTime = thermometerRepository.readInternalClock(deviceAddress = deviceAddress)
            _state.update {
                it.copy(
                    thermometerDateTime = deviceTime
                )
            }
        }
    }

    fun onEvent(event: ConnectThermometerTimeEvent) {
        when (event) {
            is ConnectThermometerTimeEvent.SelectedOptionChanged -> {
                _state.update {
                    it.copy(
                        selectedOption = event.index
                    )
                }
            }

            ConnectThermometerTimeEvent.CloseTimePicker -> _state.update {
                it.copy(
                    isTimePickerOpened = false
                )
            }

            ConnectThermometerTimeEvent.OpenDatePicker -> _state.update {
                it.copy(
                    isDatePickerOpened = true
                )
            }

            ConnectThermometerTimeEvent.CloseDatePicker -> _state.update {
                it.copy(
                    isDatePickerOpened = false
                )
            }

            is ConnectThermometerTimeEvent.DatePicked -> _state.update {
                it.copy(
                    userPickedDateTime = event.date.atTime(0, 0),
                    isDatePickerOpened = false,
                    isTimePickerOpened = true
                )
            }

            is ConnectThermometerTimeEvent.TimePicked -> _state.update { stateValue ->
                stateValue.userPickedDateTime?.date?.let { dateValue ->
                    stateValue.copy(
                        userPickedDateTime = event.time.atDate(dateValue),
                        isTimePickerOpened = false
                    )
                } ?: run {
                    stateValue
                }
            }
        }
    }
}

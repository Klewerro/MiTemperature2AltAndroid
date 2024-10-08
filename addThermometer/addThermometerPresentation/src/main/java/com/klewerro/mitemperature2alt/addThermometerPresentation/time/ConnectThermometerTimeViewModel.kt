package com.klewerro.mitemperature2alt.addThermometerPresentation.time

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.coreUi.UiConstants
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atDate
import kotlinx.datetime.atTime

@HiltViewModel
class ConnectThermometerTimeViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val thermometerRepository: ThermometerRepository
) : ViewModel() {
    private val deviceAddress = savedState.get<String>(UiConstants.NAV_PARAM_ADDRESS)!!
    private val _state = MutableStateFlow(ConnectThermometerTimeState())
    val state = _state.asStateFlow()

    private val dataSendChannel = Channel<Boolean>()
    val dataSend = dataSendChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
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
                        dateTimeType = event.dateTimeType
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
                    userProvidedDateTime = event.date.atTime(0, 0),
                    isDatePickerOpened = false,
                    isTimePickerOpened = true
                )
            }

            is ConnectThermometerTimeEvent.TimePicked -> timePicked(event.time)

            ConnectThermometerTimeEvent.SendTimeToThermometer -> sendTimeToThermometerAndContinue()
        }
    }

    private fun timePicked(time: LocalTime) {
        _state.update { stateValue ->
            stateValue.userProvidedDateTime?.date?.let { dateValue ->
                stateValue.copy(
                    userProvidedDateTime = time.atDate(dateValue),
                    isTimePickerOpened = false
                )
            } ?: run {
                stateValue
            }
        }
    }

    private fun sendTimeToThermometerAndContinue() {
        _state.update {
            it.copy(
                sendingTime = true
            )
        }
        getSelectedDateTime()?.let { dateTimeToSave ->
            viewModelScope.launch(Dispatchers.IO) {
                thermometerRepository.writeInternalClock(
                    deviceAddress = deviceAddress,
                    dateTime = dateTimeToSave
                )
                dataSendChannel.send(true)
                _state.update {
                    it.copy(
                        sendingTime = false
                    )
                }
            }
        }
    }

    private fun getSelectedDateTime(): LocalDateTime? {
        with(state.value) {
            return when (dateTimeType) {
                DateTimeType.DEVICE -> deviceDateTime
                DateTimeType.THERMOMETER -> thermometerDateTime
                DateTimeType.USER_PROVIDED -> userProvidedDateTime
            }
        }
    }
}

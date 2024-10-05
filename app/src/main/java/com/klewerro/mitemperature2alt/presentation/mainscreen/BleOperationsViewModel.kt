package com.klewerro.mitemperature2alt.presentation.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.util.UiText
import com.klewerro.mitemperature2alt.domain.model.Thermometer
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import com.klewerro.mitemperature2alt.domain.usecase.GetHourlyResultsUseCase
import com.klewerro.mitemperature2alt.domain.usecase.ScanAndConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.ThermometerListUseCase
import com.klewerro.mitemperature2alt.domain.util.DispatcherProvider
import com.klewerro.mitemperature2alt.temperatureSensor.BleConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BleOperationsViewModel @Inject constructor(
    thermometerListUseCase: ThermometerListUseCase,
    private val thermometerRepository: ThermometerRepository,
    private val scanAndConnectToDeviceUseCase: ScanAndConnectToDeviceUseCase,
    private val getHourlyResultsUseCase: GetHourlyResultsUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private var getHourlyRecordsJob: Job? = null
    private var deviceConnectionJobs: MutableMap<String, Job> = mutableMapOf()

    private val _state = MutableStateFlow(BleOperationsState())
    val state = combine(
        _state,
        thermometerListUseCase()
    ) { stateValue, thermometers ->
        stateValue.copy(
            thermometers = thermometers
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BleOperationsState())

    fun onEvent(event: BleOperationsEvent) {
        when (event) {
            is BleOperationsEvent.ErrorConnectingToSavedThermometer -> _state.update {
                it.copy(
                    error = UiText.StringResource(R.string.thermometer_is_already_saved)
                )
            }

            is BleOperationsEvent.ConnectToDevice -> {
                changeThermometerOperationType(
                    ThermometerOperationType.ConnectingToDevice(event.thermometer.name)
                )
                viewModelScope.launch(dispatchers.io) {
                    scanAndConnectToDeviceUseCase(this, event.thermometer.address)
                        .onFailure { throwable ->
                            _state.update {
                                it.copy(
                                    error = when (throwable) {
                                        is TimeoutCancellationException -> {
                                            with(event.thermometer) {
                                                Timber.i("Couldn't connect to $name ($address)")
                                                UiText.StringResource(
                                                    R.string.connect_thermometer_timeout_message,
                                                    listOf(
                                                        name,
                                                        BleConstants.CONNECT_THERMOMETER_TIMEOUT /
                                                            1_000
                                                    )
                                                )
                                            }
                                        }

                                        else -> {
                                            UiText.StringResource(
                                                R.string.unexpected_error_during_connecting_to_device
                                            )
                                        }
                                    }
                                )
                            }
                            changeThermometerOperationType(ThermometerOperationType.Idle)
                            this.coroutineContext.job.cancelAndJoin()
                        }
                        .onSuccess {
                            deviceConnectionJobs[event.thermometer.address] =
                                this.coroutineContext.job
                            changeThermometerOperationType(ThermometerOperationType.Idle)
                        }
                }
            }

            BleOperationsEvent.ErrorDismissed -> {
                _state.update {
                    it.copy(
                        error = null
                    )
                }
            }

            BleOperationsEvent.CancelHourlyRecordsSync -> {
                getHourlyRecordsJob?.cancel()
                getHourlyRecordsJob = null
                changeThermometerOperationType(ThermometerOperationType.Idle)
            }

            is BleOperationsEvent.SyncHourlyRecords -> getHourlyRecords(event.thermometer)
            is BleOperationsEvent.Disconnect -> disconnectThermometer(event.thermometer)
        }
    }

    private fun getHourlyRecords(thermometer: Thermometer) {
        getHourlyRecordsJob = viewModelScope.launch(dispatchers.io) {
            getHourlyResultsUseCase(this, thermometer.address) { progressCallback ->
                changeThermometerOperationType(
                    ThermometerOperationType.RetrievingHourlyRecords(
                        thermometer = thermometer,
                        currentRecordNumber = progressCallback.currentItemNumber,
                        numberOrRecords = progressCallback.itemsCount
                    )
                )
            }
            changeThermometerOperationType(ThermometerOperationType.Idle)
        }
    }

    private fun disconnectThermometer(thermometer: Thermometer) {
        viewModelScope.launch(dispatchers.io) {
            thermometerRepository.disconnect(thermometer.address)
            deviceConnectionJobs[thermometer.address]?.cancelAndJoin()
            deviceConnectionJobs.remove(thermometer.address)
        }
    }

    private fun changeThermometerOperationType(operationType: ThermometerOperationType) {
        _state.update {
            it.copy(thermometerOperationType = operationType)
        }
    }
}

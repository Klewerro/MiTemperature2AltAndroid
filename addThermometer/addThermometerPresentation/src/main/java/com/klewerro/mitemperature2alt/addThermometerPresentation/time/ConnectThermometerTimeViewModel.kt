package com.klewerro.mitemperature2alt.addThermometerPresentation.time

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectThermometerTimeViewModel @Inject constructor(
    private val thermometerRepository: ThermometerRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ConnectThermometerTimeState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1_000)
            _state.update {
                it.copy(
                    thermometerTime = LocalDateTimeUtils.getCurrentUtcTime()
                )
            }
        }
    }

    fun selectOption(index: Int) {
        _state.update {
            it.copy(
                selectedOption = index
            )
        }
    }
}

package com.klewerro.mitemperature2alt.thermometerDetails.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing

@Composable
fun ThermometerDetailsScreen(
    modifier: Modifier = Modifier,
    thermometerDetailsViewModel: ThermometerDetailsViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val thermometerDetailsState by thermometerDetailsViewModel.state.collectAsStateWithLifecycle()
    ThermometerDetailsScreenContent(
        state = thermometerDetailsState,
        modifier = modifier.fillMaxSize().padding(spacing.spaceScreen)
    )
}

@Composable
private fun ThermometerDetailsScreenContent(
    state: ThermometerDetailsState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        state.thermometer?.let {
            Column {
                Text(text = state.thermometer.name)
                Text(text = state.thermometer.address)
            }
        } ?: run {
            Text(text = "Unexpected error.")
        }
    }
}

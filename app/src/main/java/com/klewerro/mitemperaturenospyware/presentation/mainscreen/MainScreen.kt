@file:OptIn(ExperimentalMaterialApi::class)

package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar {
                scope.launch {
                    modalBottomSheetState.show()
                }
            }
        }
    ) { scaffoldPadding ->
        Surface(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            AddHeaterBottomSheet(modalBottomSheetState) {
                MainScreenContent()
            }
        }
    }
}

@Composable
fun MainScreenContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "MainScreen")
    }
}

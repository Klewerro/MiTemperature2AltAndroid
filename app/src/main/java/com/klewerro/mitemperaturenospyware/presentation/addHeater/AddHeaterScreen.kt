package com.klewerro.mitemperaturenospyware.presentation.addHeater

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperaturenospyware.R
import com.klewerro.mitemperaturenospyware.presentation.addHeater.components.DevicesList
import com.klewerro.mitemperaturenospyware.presentation.addHeater.components.PermissionDeclinedRationale
import com.klewerro.mitemperaturenospyware.presentation.mainscreen.DeviceSearchViewModel
import com.klewerro.mitemperaturenospyware.presentation.model.PermissionStatus
import com.klewerro.mitemperaturenospyware.presentation.util.getActivity
import com.klewerro.mitemperaturenospyware.presentation.util.isAndroid12OrGreater
import com.klewerro.mitemperaturenospyware.ui.LocalSpacing

@Composable
fun AddHeaterScreen(
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    viewModel: DeviceSearchViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val activity = context.getActivity()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    val scannedDevices by viewModel.devicesCombined.collectAsStateWithLifecycle()
    val isScanningForDevices by viewModel.isScanningForDevices.collectAsStateWithLifecycle()

    var wasAppSettingsCheckClicked by remember {
        mutableStateOf(false)
    }

    val nearbyDevicesPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { grantedMap ->
            val areAllGranted = grantedMap.all { it.value }
            val isPermanentlyDeclined = !ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                if (isAndroid12OrGreater()) {
                    Manifest.permission.BLUETOOTH_SCAN
                } else {
                    Manifest.permission.ACCESS_COARSE_LOCATION
                }
            )
            viewModel.permissionGrantStatus = when {
                areAllGranted -> PermissionStatus.GRANTED
                !areAllGranted && !isPermanentlyDeclined -> PermissionStatus.DECLINED
                else -> PermissionStatus.PERMANENTLY_DECLINED
            }
        }
    )

    LaunchedEffect(key1 = viewModel.permissionGrantStatus) {
        if (viewModel.permissionGrantStatus == PermissionStatus.DECLINED) {
            nearbyDevicesPermissionResultLauncher.launchRequestBlePermissions()
        }
    }

    LaunchedEffect(key1 = lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            if (viewModel.permissionGrantStatus == PermissionStatus.PERMANENTLY_DECLINED && wasAppSettingsCheckClicked) {
                wasAppSettingsCheckClicked = false
                nearbyDevicesPermissionResultLauncher.launchRequestBlePermissions()
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiTextError.collect { uiText ->
            scaffoldState.snackbarHostState.showSnackbar(
                message = uiText.asString(context)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.spaceNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (viewModel.permissionGrantStatus) {
            PermissionStatus.GRANTED -> {
                DevicesList(
                    isScanningForDevices = isScanningForDevices,
                    scannedDevices = scannedDevices,
                    onButtonClickWhenScanning = viewModel::stopScanForDevices,
                    onButtonClickWhenNotScanning = { viewModel.scanForDevices() },
                    onDeviceClick = { thermometerUiDevice ->
                        viewModel.connectToDevice(thermometerUiDevice)
                    }
                )
            }
            PermissionStatus.DECLINED -> {
                PermissionDeclinedRationale(
                    rationaleAndroid12Text = stringResource(R.string.ble_permissions_not_granted_rationale),
                    rationalePreAndroid12Text = stringResource(R.string.location_permissions_not_granted_rationale),
                    buttonText = stringResource(R.string.ask_again),
                    onButtonClick = nearbyDevicesPermissionResultLauncher::launchRequestBlePermissions
                )
            }
            PermissionStatus.PERMANENTLY_DECLINED -> {
                PermissionDeclinedRationale(
                    stringResource(R.string.ble_permissions_permanently_declined),
                    stringResource(R.string.location_permissions_permanently_declined),
                    stringResource(R.string.open_app_settings),
                    onButtonClick = {
                        activity.openAppSettings()
                        wasAppSettingsCheckClicked = true
                    }
                )
            }
        }
    }
}

private fun ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>.launchRequestBlePermissions() {
    this.launch(
        if (isAndroid12OrGreater()) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    )
}

private fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(this::startActivity)
}

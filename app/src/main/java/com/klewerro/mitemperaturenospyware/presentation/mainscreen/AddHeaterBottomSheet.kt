package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klewerro.mitemperaturenospyware.R
import com.klewerro.mitemperaturenospyware.presentation.getActivity
import com.klewerro.mitemperaturenospyware.presentation.model.PermissionStatus
import com.klewerro.mitemperaturenospyware.ui.LocalSpacing

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddHeaterBottomSheet(
    modalBottomSheetState: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val activity = context.getActivity()
    val viewModel = viewModel<DeviceSearchViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    val scannedDevices by viewModel.scannedDevices.collectAsStateWithLifecycle()
    val isScanningForDevices by viewModel.isScanningForDevices.collectAsStateWithLifecycle()

    var wasAppSettingsCheckClicked by remember {
        mutableStateOf(false)
    }

    val nearbyDevicesPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { grantedMap ->
            val areAllGranted = grantedMap.all { it.value }
            val isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.BLUETOOTH_SCAN
            )
            viewModel.permissionGrantStatus = when {
                areAllGranted -> PermissionStatus.GRANTED
                !areAllGranted && !isPermanentlyDeclined -> PermissionStatus.DECLINED
                else -> PermissionStatus.PERMANENTLY_DECLINED
            }
        }
    )

    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(
            topStart = spacing.radiusNormal,
            topEnd = spacing.radiusNormal
        ),
        sheetState = modalBottomSheetState,
        sheetContent = {
            LaunchedEffect(key1 = viewModel.permissionGrantStatus) {
                if (viewModel.permissionGrantStatus == PermissionStatus.DECLINED) {
                    nearbyDevicesPermissionResultLauncher.launch(
                        arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                        )
                    )
                }
            }

            LaunchedEffect(key1 = lifecycleState) {
                if (lifecycleState == Lifecycle.State.RESUMED) {
                    if (viewModel.permissionGrantStatus == PermissionStatus.PERMANENTLY_DECLINED && wasAppSettingsCheckClicked) {
                        wasAppSettingsCheckClicked = false
                        nearbyDevicesPermissionResultLauncher.launch(
                            arrayOf(
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calculateSheetHeight(scannedDevices.size))
                    .clip(
                        RoundedCornerShape(
                            topStart = spacing.radiusNormal,
                            topEnd = spacing.radiusNormal
                        )
                    )
                    .background(MaterialTheme.colors.primary)
                    .padding(spacing.spaceNormal),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (viewModel.permissionGrantStatus) {
                    PermissionStatus.GRANTED -> {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onPrimary),
                            onClick = {
                                if (isScanningForDevices) {
                                    viewModel.stopScanForDevices()
                                } else {
                                    viewModel.scanForDevices(context)
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(
                                    if (isScanningForDevices) R.string.stop_scan else R.string.scan_for_devices
                                )
                            )
                        }
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(scannedDevices) { thermometerBleDevice ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colors.onPrimary)
                                        .padding(8.dp)
                                        .clickable {
                                            viewModel.connectToDevice(context, thermometerBleDevice)
                                        }
                                ) {
                                    Text(text = thermometerBleDevice.name)
                                    Text(text = thermometerBleDevice.address)
                                    Text(text = thermometerBleDevice.rssi.toString())
                                }
                            }
                        }
                    }
                    PermissionStatus.DECLINED -> {
                        Text(
                            text = stringResource(R.string.ble_permissions_not_granted_rationale)
                        )
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onPrimary),
                            onClick = {
                                nearbyDevicesPermissionResultLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.BLUETOOTH_SCAN,
                                        Manifest.permission.BLUETOOTH_CONNECT
                                    )
                                )
                            }
                        ) {
                            Text(text = stringResource(R.string.ask_again))
                        }
                    }
                    PermissionStatus.PERMANENTLY_DECLINED -> {
                        Text(
                            text = stringResource(R.string.ble_permissions_permanently_declined)
                        )
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onPrimary),
                            onClick = {
                                activity.openAppSettings()
                                wasAppSettingsCheckClicked = true
                            }
                        ) {
                            Text(text = stringResource(R.string.open_app_settings))
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
}

private fun calculateSheetHeight(devicesCount: Int): Dp {
    val baseHeight = 200
    val singleItemHeight = 72
    val maxCountMultiplier = 3
    val combinedHeight = if (devicesCount <= maxCountMultiplier) {
        baseHeight + devicesCount * singleItemHeight
    } else {
        baseHeight + devicesCount * singleItemHeight * maxCountMultiplier
    }

    return combinedHeight.dp
}

private fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(this::startActivity)
}

package com.klewerro.mitemperature2alt.presentation.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavController
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.UiConstants

sealed class Route(
    @StringRes val screenName: Int,
    private val route: String,
    vararg params: String
) {
    val fullRoute: String = if (params.isEmpty()) {
        route
    } else {
        val builder = StringBuilder(route)
        params.forEach { builder.append("/{$it}") }
        builder.toString()
    }

    fun navigate(navController: NavController, vararg params: Any) {
        val address = if (params.isEmpty()) {
            route
        } else {
            route + "/" + params.joinToString("/")
        }
        navController.navigate(address)
    }

    sealed class MainRoutes {
        data object Main : Route(R.string.title_mi_temperature_2_alt, "main_screen")
        data object ScanForDevices : Route(R.string.scan_for_devices, "device_scan")
    }

    sealed class ConnectDeviceRoutes {
        data object ConnectDeviceGraph : Route(
            R.string.connect,
            "connect",
            UiConstants.NAV_PARAM_ADDRESS
        )

        data object Connecting : Route(R.string.connecting_to_thermometer, "connect_connecting")
        data object SetTime : Route(R.string.set_thermometer_name, "connect_time")
        data object SetName : Route(R.string.set_thermometer_name, "connect_name")
    }

    sealed class ThermometerDetails {
        data object HourlyRecords : Route(
            R.string.details,
            "hourly_records",
            UiConstants.NAV_PARAM_ADDRESS
        )
    }
}

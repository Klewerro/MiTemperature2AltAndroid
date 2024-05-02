package com.klewerro.mitemperature2alt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.klewerro.mitemperature2alt.presentation.addHeater.AddThermometerScreen
import com.klewerro.mitemperature2alt.presentation.addHeater.DeviceSearchViewModel
import com.klewerro.mitemperature2alt.presentation.addHeater.connecting.ConnectThermometerScreen
import com.klewerro.mitemperature2alt.presentation.addHeater.connecting.ConnectThermometerViewModel
import com.klewerro.mitemperature2alt.presentation.mainscreen.BleOperationsViewModel
import com.klewerro.mitemperature2alt.presentation.mainscreen.MainScreen
import com.klewerro.mitemperature2alt.presentation.mainscreen.TopBar
import com.klewerro.mitemperature2alt.presentation.navigation.Route
import com.klewerro.mitemperature2alt.ui.theme.MiTemperature2AltTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiTemperature2AltTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val bleOperationsViewModel: BleOperationsViewModel = hiltViewModel()
                val bleOperationsSate by bleOperationsViewModel.state.collectAsStateWithLifecycle()

                var titleState by remember {
                    mutableStateOf(Route.MAIN.screenName)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState,
                    topBar = {
                        TopBar(
                            title = titleState,
                            shouldBeButtonVisible = titleState == Route.MAIN.screenName
                        ) {
                            navController.navigate(Route.SCAN_FOR_DEVICES.name)
                        }
                    }
                ) { scaffoldPadding ->
                    Surface(
                        modifier = Modifier
                            .padding(scaffoldPadding)
                            .fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Route.MAIN.name
                        ) {
                            // Animations: https://proandroiddev.com/screen-transition-animations-with-jetpack-navigation-17afdc714d0e
                            composable(Route.MAIN.name) {
                                MainScreen(
                                    state = bleOperationsSate,
                                    onEvent = { event ->
                                        bleOperationsViewModel.onEvent(event)
                                    },
                                    scaffoldState = scaffoldState
                                )
                                titleState = Route.MAIN.screenName
                            }
                            composable(Route.SCAN_FOR_DEVICES.name) {
                                val deviceSearchViewModel = hiltViewModel<DeviceSearchViewModel>()
                                val deviceSearchState by deviceSearchViewModel.state
                                    .collectAsStateWithLifecycle()
                                AddThermometerScreen(
                                    bleOperationsState = bleOperationsSate,
                                    onBleOperationsEvent = { bleOperationsEvent ->
                                        bleOperationsViewModel.onEvent(bleOperationsEvent)
                                    },
                                    deviceSearchState = deviceSearchState,
                                    onDeviceSearchEvent = { deviceSearchEvent ->
                                        deviceSearchViewModel.onEvent(deviceSearchEvent)
                                    },
                                    onDeviceListItemClick = {
                                        navController.navigate(
                                            route = "connect/${it.address}"
                                        )
                                    },
                                    scaffoldState = scaffoldState
                                )
                                titleState = Route.SCAN_FOR_DEVICES.screenName
                            }

                            connectThermometerGraph(navController)
                        }
                    }
                }
            }
        }
    }

    private fun NavGraphBuilder.connectThermometerGraph(navController: NavController) {
        navigation(
            route = "connect/{address}",
            startDestination = "connect_connecting",
            arguments = listOf(
                navArgument("address") {
                    type = NavType.StringType
                }
            )

        ) {
            composable(
                route = "connect_connecting"
            ) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(
                        "connect/{address}"
                    )
                }
                val address = parentEntry.arguments?.getString("address")
                val connectThermometerViewModel: ConnectThermometerViewModel =
                    hiltViewModel(parentEntry)
                connectThermometerViewModel.saveThermometerAddress = address
                ConnectThermometerScreen(connectThermometerViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MiTemperature2AltTheme {
        Greeting("Android")
    }
}

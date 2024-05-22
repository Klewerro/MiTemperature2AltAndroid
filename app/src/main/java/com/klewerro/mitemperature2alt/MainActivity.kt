package com.klewerro.mitemperature2alt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.presentation.addThermometer.ConnectThermometerViewModel
import com.klewerro.mitemperature2alt.presentation.addThermometer.connecting.ThermometerConnectingScreen
import com.klewerro.mitemperature2alt.presentation.addThermometer.name.ConnectThermometerNameScreen
import com.klewerro.mitemperature2alt.presentation.addThermometer.search.DeviceSearchViewModel
import com.klewerro.mitemperature2alt.presentation.addThermometer.search.SearchThermometersScreen
import com.klewerro.mitemperature2alt.presentation.mainscreen.BleOperationsViewModel
import com.klewerro.mitemperature2alt.presentation.mainscreen.MainScreen
import com.klewerro.mitemperature2alt.presentation.mainscreen.TopBar
import com.klewerro.mitemperature2alt.presentation.navigation.Route
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

                var titleResourceIdState by remember {
                    mutableIntStateOf(Route.MainRoutes.Main.screenName)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState,
                    topBar = {
                        TopBar(
                            title = stringResource(titleResourceIdState),
                            shouldBeButtonVisible = titleResourceIdState == Route.MainRoutes.Main.screenName
                        ) {
                            Route.MainRoutes.ScanForDevices.navigate(navController)
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
                            startDestination = Route.MainRoutes.Main.fullRoute
                        ) {
                            // Animations: https://proandroiddev.com/screen-transition-animations-with-jetpack-navigation-17afdc714d0e
                            composable(Route.MainRoutes.Main.fullRoute) {
                                MainScreen(
                                    state = bleOperationsSate,
                                    onEvent = { event ->
                                        bleOperationsViewModel.onEvent(event)
                                    },
                                    scaffoldState = scaffoldState
                                )
                                titleResourceIdState = Route.MainRoutes.Main.screenName
                            }
                            composable(Route.MainRoutes.ScanForDevices.fullRoute) {
                                val deviceSearchViewModel = hiltViewModel<DeviceSearchViewModel>()
                                val deviceSearchState by deviceSearchViewModel.state
                                    .collectAsStateWithLifecycle()
                                SearchThermometersScreen(
                                    bleOperationsState = bleOperationsSate,
                                    onBleOperationsEvent = { bleOperationsEvent ->
                                        bleOperationsViewModel.onEvent(bleOperationsEvent)
                                    },
                                    deviceSearchState = deviceSearchState,
                                    onDeviceSearchEvent = { deviceSearchEvent ->
                                        deviceSearchViewModel.onEvent(deviceSearchEvent)
                                    },
                                    onDeviceListItemClick = {
                                        Route.ConnectDeviceRoutes.ConnectDeviceGraph.navigate(
                                            navController,
                                            it.address
                                        )
                                    },
                                    scaffoldState = scaffoldState
                                )
                                titleResourceIdState = Route.MainRoutes.ScanForDevices.screenName
                            }

                            connectThermometerGraph(navController, scaffoldState)
                        }
                    }
                }
            }
        }
    }

    private fun NavGraphBuilder.connectThermometerGraph(
        navController: NavController,
        scaffoldState: ScaffoldState
    ) {
        navigation(
            route = Route.ConnectDeviceRoutes.ConnectDeviceGraph.fullRoute,
            startDestination = Route.ConnectDeviceRoutes.Connecting.fullRoute,
            arguments = listOf(
                navArgument(Route.ConnectDeviceRoutes.PARAM_ADDRESS) {
                    type = NavType.StringType
                }
            )

        ) {
            composable(
                route = Route.ConnectDeviceRoutes.Connecting.fullRoute
            ) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(
                        Route.ConnectDeviceRoutes.ConnectDeviceGraph.fullRoute
                    )
                }
                val connectThermometerViewModel: ConnectThermometerViewModel =
                    hiltViewModel(parentEntry)
                ThermometerConnectingScreen(
                    connectThermometerViewModel,
                    onError = {
                        navController.popBackStack()
                    },
                    onDeviceConnected = {
                        navController.navigate(
                            Route.ConnectDeviceRoutes.SetName.fullRoute
                        ) {
                            // Removes ConnectThermometerScreen backstack
                            popUpTo(Route.ConnectDeviceRoutes.Connecting.fullRoute) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(Route.ConnectDeviceRoutes.SetName.fullRoute) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(
                        Route.ConnectDeviceRoutes.ConnectDeviceGraph.fullRoute
                    )
                }
                val connectThermometerViewModel: ConnectThermometerViewModel =
                    hiltViewModel(parentEntry)
                ConnectThermometerNameScreen(
                    viewModel = connectThermometerViewModel,
                    onThermometerSaved = {
                        navController.popBackStack(
                            Route.MainRoutes.Main.fullRoute,
                            false
                        )
                    }
                )
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

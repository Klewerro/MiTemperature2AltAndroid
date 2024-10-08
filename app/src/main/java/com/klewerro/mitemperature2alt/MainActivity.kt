package com.klewerro.mitemperature2alt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.klewerro.mitemperature2alt.addThermometerPresentation.ConnectThermometerViewModel
import com.klewerro.mitemperature2alt.addThermometerPresentation.ThermometerConnectingScreen
import com.klewerro.mitemperature2alt.addThermometerPresentation.name.ConnectThermometerNameScreen
import com.klewerro.mitemperature2alt.addThermometerPresentation.search.SearchThermometersScreen
import com.klewerro.mitemperature2alt.addThermometerPresentation.time.ConnectThermometerTimeScreen
import com.klewerro.mitemperature2alt.coreUi.UiConstants
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.presentation.bottomSheet.BottomSheetContent
import com.klewerro.mitemperature2alt.presentation.mainscreen.BleOperationsEvent
import com.klewerro.mitemperature2alt.presentation.mainscreen.BleOperationsViewModel
import com.klewerro.mitemperature2alt.presentation.mainscreen.MainScreen
import com.klewerro.mitemperature2alt.presentation.mainscreen.ThermometerOperationType
import com.klewerro.mitemperature2alt.presentation.mainscreen.TopBar
import com.klewerro.mitemperature2alt.presentation.navigation.Route
import com.klewerro.mitemperature2alt.thermometerDetails.presentation.ThermometerDetailsScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiTemperature2AltTheme {
                val navController = rememberNavController()
                val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = rememberBottomSheetState(
                        initialValue = BottomSheetValue.Collapsed
                    )
                )
                val bleOperationsViewModel: BleOperationsViewModel = hiltViewModel()
                val bleOperationsSate by bleOperationsViewModel.state.collectAsStateWithLifecycle()

                var titleResourceIdState by remember {
                    mutableIntStateOf(Route.MainRoutes.Main.screenName)
                }

                BottomSheetScaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = bottomSheetScaffoldState,
                    sheetContent = {
                        BottomSheetContent(
                            thermometerOperationType = bleOperationsSate.thermometerOperationType,
                            thermometers = bleOperationsSate.thermometers,
                            thermometerWithRunningOperation = with(
                                bleOperationsSate.thermometerOperationType
                            ) {
                                when (this) {
                                    is ThermometerOperationType.RetrievingHourlyRecords -> {
                                        this.thermometer
                                    }

                                    else -> null
                                }
                            },

                            onConnectThermometerClick = { thermometer ->
                                bleOperationsViewModel.onEvent(
                                    BleOperationsEvent.ConnectToDevice(thermometer)
                                )
                            },
                            onThermometerCancelButtonClick = {
                                bleOperationsViewModel.onEvent(
                                    BleOperationsEvent.CancelHourlyRecordsSync
                                )
                            },
                            onSyncThermometerClick = {
                                bleOperationsViewModel.onEvent(
                                    BleOperationsEvent.SyncHourlyRecords(it)
                                )
                            },
                            onDisconnectThermometerClick = {
                                bleOperationsViewModel.onEvent(
                                    BleOperationsEvent.Disconnect(it)
                                )
                            }
                        )
                    },
                    sheetShape = RoundedCornerShape(12.dp),
                    sheetPeekHeight = 48.dp,
                    topBar = {
                        TopBar(
                            title = stringResource(titleResourceIdState),
                            shouldBeButtonVisible =
                            titleResourceIdState == Route.MainRoutes.Main.screenName
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
                                    onThermometerClick = { thermometer ->
                                        Route.ThermometerDetails.HourlyRecords.navigate(
                                            navController,
                                            thermometer.address
                                        )
                                    },
                                    snackbarHostState = bottomSheetScaffoldState.snackbarHostState
                                )
                                titleResourceIdState = Route.MainRoutes.Main.screenName
                            }
                            composable(Route.MainRoutes.ScanForDevices.fullRoute) {
                                SearchThermometersScreen(
                                    bottomSheetScaffoldState.snackbarHostState,
                                    onDeviceListItemClick = { clickedDeviceAddress ->
                                        Route.ConnectDeviceRoutes.ConnectDeviceGraph.navigate(
                                            navController,
                                            clickedDeviceAddress
                                        )
                                    }
                                )
                                titleResourceIdState = Route.MainRoutes.ScanForDevices.screenName
                            }

                            connectThermometerGraph(
                                navController,
                                bleOperationsViewModel.viewModelScope
                            )

                            composable(
                                route = Route.ThermometerDetails.HourlyRecords.fullRoute,
                                arguments = listOf(
                                    navArgument(UiConstants.NAV_PARAM_ADDRESS) {
                                        type = NavType.StringType
                                    }
                                )
                            ) {
                                titleResourceIdState =
                                    Route.ThermometerDetails.HourlyRecords.screenName
                                ThermometerDetailsScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun NavGraphBuilder.connectThermometerGraph(
        navController: NavController,
        bleOperationsViewModelCoroutineScope: CoroutineScope
    ) {
        navigation(
            route = Route.ConnectDeviceRoutes.ConnectDeviceGraph.fullRoute,
            startDestination = Route.ConnectDeviceRoutes.Connecting.fullRoute,
            arguments = listOf(
                navArgument(UiConstants.NAV_PARAM_ADDRESS) {
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
                    bleViewModelScope = bleOperationsViewModelCoroutineScope,
                    onError = {
                        navController.popBackStack()
                    },
                    onDeviceConnected = {
                        navController.navigate(
                            "${Route.ConnectDeviceRoutes.SetTime.route}/${connectThermometerViewModel.state.value.thermometerAddress}"
                        ) {
                            // Removes ConnectThermometerScreen backstack
                            popUpTo(Route.ConnectDeviceRoutes.Connecting.fullRoute) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(
                route = Route.ConnectDeviceRoutes.SetTime.fullRoute,
                arguments = listOf(
                    navArgument(UiConstants.NAV_PARAM_ADDRESS) {
                        type = NavType.StringType
                    }
                )
            ) {
                ConnectThermometerTimeScreen(
                    onNextButtonClick = {
                        navController.navigate(
                            Route.ConnectDeviceRoutes.SetName.fullRoute
                        )
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

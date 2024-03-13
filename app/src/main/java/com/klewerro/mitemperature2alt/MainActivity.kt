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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klewerro.mitemperature2alt.presentation.addHeater.AddThermometerScreen
import com.klewerro.mitemperature2alt.presentation.mainscreen.BleOperationsViewModel
import com.klewerro.mitemperature2alt.presentation.mainscreen.MainScreen
import com.klewerro.mitemperature2alt.presentation.mainscreen.TopBar
import com.klewerro.mitemperature2alt.presentation.navigation.Route
import com.klewerro.mitemperature2alt.ui.theme.MiTemperatureNoSpywareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiTemperatureNoSpywareTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val bleOperationsViewModel: BleOperationsViewModel = hiltViewModel()

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
                                MainScreen(bleOperationsViewModel)
                                titleState = Route.MAIN.screenName
                            }
                            composable(Route.SCAN_FOR_DEVICES.name) {
                                AddThermometerScreen(
                                    scaffoldState = scaffoldState,
                                    bleOperationsViewModel = bleOperationsViewModel
                                )
                                titleState = Route.SCAN_FOR_DEVICES.screenName
                            }
                        }
                    }
                }
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
    MiTemperatureNoSpywareTheme {
        Greeting("Android")
    }
}

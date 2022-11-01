package com.a6.bluetoothservice.navigation

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.a6.bluetoothservice.bluetooth.lowenergy.BluetoothLEViewModel
import com.a6.bluetoothservice.screens.DeviceLEScreen
import com.a6.bluetoothservice.screens.ShowDevices

@Composable
fun AppNavigation(viewModel: BluetoothLEViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.ShowDevices.baseRoute) {

        composable(route = AppScreens.ShowDevices.baseRoute) {
            // val viewModel = hiltViewModel<BluetoothLEViewModel>()
            ShowDevices(navController = navController, viewModel = viewModel)
        }

        composable(
            route = AppScreens.ShowDevice.route,
            arguments = AppScreens.ShowDevice.args
        ) {
            val mac = it.arguments?.getString(NavArgs.Mac.key)
            requireNotNull(mac) { "La cagaste en AppNavigation con la mac" }
            DeviceLEScreen(viewModel = viewModel, mac)
        }

    }

}

@Composable
fun Toolbar() {
    TopAppBar(title = {
        Text(text = "Bluetooth 2022")
    })
}
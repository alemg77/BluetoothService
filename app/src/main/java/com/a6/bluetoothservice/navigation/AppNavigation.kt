package com.a6.bluetoothservice.navigation

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.a6.bluetoothservice.bluetooth.BluetoothAndroidViewModel
import com.a6.bluetoothservice.screens.DeviceScreen
import com.a6.bluetoothservice.screens.ShowDevices

@Composable
fun AppNavigation(viewModel: BluetoothAndroidViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.ShowDevices.baseRoute) {

        composable(route = AppScreens.ShowDevices.baseRoute) {
            // val viewModel = hiltViewModel<BluetoothAndroidViewModel>()
            ShowDevices(navController = navController, viewModel = viewModel)
        }

        composable(
            route = AppScreens.ShowDevice.route,
            arguments = AppScreens.ShowDevice.args
        ) {
            val name = it.arguments?.getString(NavArgs.Name.key)
            requireNotNull(name) { "La cagaste en AppNavigation con el name" }
            val mac = it.arguments?.getString(NavArgs.Mac.key)
            requireNotNull(mac) { "La cagaste en AppNavigation con la mac" }
            DeviceScreen(viewModel = viewModel, name = name, mac = mac)
        }

    }

}

@Composable
fun Toolbar() {
    TopAppBar(title = {
        Text(text = "Bluetooth 2022")
    })
}
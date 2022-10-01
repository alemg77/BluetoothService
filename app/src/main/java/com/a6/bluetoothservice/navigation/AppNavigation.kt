package com.a6.bluetoothservice.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.a6.bluetoothservice.bluetooth.BluetoothAndroidViewModel
import com.a6.bluetoothservice.screens.ShowDevice
import com.a6.bluetoothservice.screens.ShowDevices

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.ShowDevices.baseRoute) {

        composable(route = AppScreens.ShowDevices.baseRoute) {
            val viewModel = hiltViewModel<BluetoothAndroidViewModel>()
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
            ShowDevice(name = name, mac = mac)
        }

    }

}

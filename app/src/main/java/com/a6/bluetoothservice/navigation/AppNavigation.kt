package com.a6.bluetoothservice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.a6.bluetoothservice.screens.ShowDevice
import com.a6.bluetoothservice.screens.ShowDevices

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.ShowDevices.route) {

        composable(route = AppScreens.ShowDevices.route) {
            ShowDevices(navController)
        }

        composable(
            route = AppScreens.ShowDevice.route + "/{mac}/{name}",
            arguments = listOf(
                navArgument(name = "mac") { type = NavType.StringType },
                navArgument(name = "name") { type = NavType.StringType }
            )
        ) {
            val name = it.arguments?.getString("name", "sin nombre!")
            it.arguments?.getString("mac")?.let { mac -> ShowDevice(mac = mac, name = name!!) }
        }

    }

}

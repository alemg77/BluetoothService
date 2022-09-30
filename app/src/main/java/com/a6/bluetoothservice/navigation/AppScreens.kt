package com.a6.bluetoothservice.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class AppScreens(
    val baseRoute: String,
    private val navArgs: List<NavArgs> = emptyList()
) {

    val route = run {
        val argKeys = navArgs.map { "{${it.key}}" }
        listOf(baseRoute).plus(argKeys).joinToString("/")
    }

    val args = navArgs.map {
        navArgument(it.key) { type = it.navType }
    }

    object ShowDevices : AppScreens("list_devices")

    object ShowDevice : AppScreens("device", listOf(NavArgs.Name, NavArgs.Mac)) {
        fun createNavRoute(name:String, mac:String) = "$baseRoute/$name/$mac"
    }

}

enum class NavArgs(val key: String, val navType: NavType<*>) {
    Name("name", NavType.StringType),
    Mac("mac", NavType.StringType)
}

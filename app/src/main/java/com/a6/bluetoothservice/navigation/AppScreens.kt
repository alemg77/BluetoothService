package com.a6.bluetoothservice.navigation

sealed class AppScreens(val route:String) {

    object ShowDevices: AppScreens("list_devices")

    object ShowDevice: AppScreens("device")

}

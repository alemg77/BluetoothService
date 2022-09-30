package com.a6.bluetoothservice.screens

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.a6.bluetoothservice.bluetooth.BluetoothAndroidViewModel

@Composable
fun ShowDevices(viewModel: BluetoothAndroidViewModel) {

    val isBluetoothOn by viewModel.isBluetoothOn.observeAsState()

    val bluetoothDevices by viewModel.bluetoothDevices.observeAsState()

    if (bluetoothDevices == null ) {
        Log.d("TAGG", "No tenemos dispocitivos")
    }
    else {
        Log.d("TAGG", "Si tenemos dispocitivos!!")
    }

    LoadingScreen(isBluetoothOn)

}
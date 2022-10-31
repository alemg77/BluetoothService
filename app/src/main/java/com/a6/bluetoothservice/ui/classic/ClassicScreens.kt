package com.a6.bluetoothservice.ui.classic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.a6.bluetoothservice.bluetooth.classic.BluetoothClassicViewModel
import com.a6.bluetoothservice.screens.ToolbarDevice

@Composable
fun ShowClassicDevice(viewModel: BluetoothClassicViewModel) {

    Scaffold(
        topBar = { ToolbarDevice(viewModel) },
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Greeting2("Android")
            }
        })

}


@Composable
fun Greeting2(name: String) {

    Text(text = "Hello $name!")

}
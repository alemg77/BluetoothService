package com.a6.bluetoothservice.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.a6.bluetoothservice.bluetooth.BluetoothAndroidViewModel
import com.a6.bluetoothservice.bluetooth.BluetoothDeviceUIModel
import com.a6.bluetoothservice.navigation.AppScreens

@Composable
fun ShowDevices(navController: NavHostController = rememberNavController()) {

    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    val viewModel = ViewModelProvider(viewModelStoreOwner)[BluetoothAndroidViewModel::class.java]

    viewModel.getBondedDevices()

    val bluetoothDevices by viewModel.bluetoothDevices.observeAsState()

    when (val it = bluetoothDevices) {

        null -> {
            LoadingScreen()
        }

        else -> {
            MyDevices(navController = navController, devices = it)
        }

    }

}

@Preview(showSystemUi = true)
@Preview(showBackground = true)
@Composable
fun MyDevices(
    navController: NavHostController = rememberNavController(),
    devices: ArrayList<BluetoothDeviceUIModel> = BluetoothDeviceUIModel.MOCK_DEVICES
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column() {
            devices.forEach {
                ListElement(navController = navController, device = it)
            }
        }
    }
}

@Composable
fun ListElement(
    navController: NavHostController,
    device: BluetoothDeviceUIModel
) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .background(MaterialTheme.colors.surface)
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    route = AppScreens.ShowDevice.createNavRoute(
                        name = device.name,
                        mac = device.mac
                    )
                )
            }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {

            Row() {
                Text(text = "nombre: ", fontSize = 20.sp)
                Text(text = device.name, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row() {
                Text(text = "mac = ", fontSize = 20.sp)
                Text(text = device.mac, fontSize = 20.sp)
            }

        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = Color.Red,
            strokeWidth = 10.dp
        )
    }
}

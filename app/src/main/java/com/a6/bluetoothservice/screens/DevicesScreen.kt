package com.a6.bluetoothservice.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.a6.bluetoothservice.bluetooth.BluetoothAndroidViewModel
import com.a6.bluetoothservice.bluetooth.BluetoothDeviceUIModel
import com.a6.bluetoothservice.navigation.AppScreens
import com.a6.bluetoothservice.navigation.Toolbar


@Composable
fun ShowDevices(
    navController: NavHostController = rememberNavController(),
    viewModel: BluetoothAndroidViewModel
) {

    val isBluetoothEnable = viewModel.isBluetoothOn.value

    if (!isBluetoothEnable) {

        LoadingScreen()

    } else {

        MyDevicesScreen(navController = navController, viewModel.bluetoothDevices)

    }

}

//@Preview(showBackground = true)
@Composable
fun MyDevicesScreen(
    navController: NavHostController = rememberNavController(),
    devices: List<BluetoothDeviceUIModel> = BluetoothDeviceUIModel.MOCK_DEVICES
) {

    Scaffold(
        topBar = { Toolbar() },
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {

                LazyColumn(modifier = Modifier.padding(4.dp)) {

                    item {
                        devices.forEach {
                            ListElement(navController = navController, device = it)
                        }

                    }
                }

            }

        })
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

            Text(text = "nombre: ${device.name}", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "mac = ${device.mac}", fontSize = 24.sp)

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



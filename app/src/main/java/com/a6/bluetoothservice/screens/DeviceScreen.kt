package com.a6.bluetoothservice.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a6.bluetoothservice.MainActivity.Companion.TAG
import com.a6.bluetoothservice.R
import com.a6.bluetoothservice.bluetooth.BluetoothAndroidViewModel

@Composable
fun DeviceScreen(
    viewModel: BluetoothAndroidViewModel = hiltViewModel(),
    mac: String = "sin mac",
    name: String = "sin nombre"
) {

    viewModel.connectGatt(mac)

    Scaffold(
        topBar = { ToolbarDevice(name = name, viewModel = viewModel, mac = mac) },
        content = {
            ContentDevice(mac, name) {
                viewModel.send(it)
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun ToolbarDevice(
    viewModel: BluetoothAndroidViewModel = hiltViewModel(),
    mac: String = "sin mac",
    name: String = "sin nombre"
) {

    val isGattConnected = viewModel.isGattConnected.value

    TopAppBar(title = {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        )
        {

            Text(
                text = name, modifier = Modifier.padding(12.dp)
            )

            Image(
                painterResource(
                    id = if (isGattConnected) {
                        R.drawable.ic_connected
                    } else {
                        R.drawable.ic_disconnected
                    }
                ),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(12.dp)
                    .clickable(
                        enabled = true,
                        onClickLabel = "Clickable image",
                        onClick = {
                            if (isGattConnected) {
                                viewModel.disConnectGatt()
                            } else {
                                viewModel.connectGatt(mac)
                            }
                        }),
                colorFilter = ColorFilter.tint(color = Color.White)
            )
        }
    })

}

@Composable
fun ContentDevice(
    mac: String,
    name: String,
    onClick: (message: String) -> Unit,
) {
    Column(modifier = Modifier.padding(8.dp)) {

        CardDevice(mac, name)

        Button(onClick = {
            onClick("Pulsador 1  ")
        }) {
            Text(text = "Pulsador 1")
        }

    }
}


@Composable
fun CardDevice(
    mac: String = "sin mac",
    name: String = "sin nombre"
) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .background(MaterialTheme.colors.surface)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {

            Row {
                Text(text = "nombre: ", fontSize = 20.sp)
                Text(text = name, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Text(text = "mac = ", fontSize = 20.sp)
                Text(text = mac, fontSize = 20.sp)
            }

        }
    }
}


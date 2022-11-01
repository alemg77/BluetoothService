package com.a6.bluetoothservice.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.a6.bluetoothservice.R
import com.a6.bluetoothservice.bluetooth.BluetoothViewModel
import com.a6.bluetoothservice.bluetooth.GattServiceUIModel
import com.a6.bluetoothservice.bluetooth.lowenergy.BluetoothLEViewModel
import com.a6.bluetoothservice.bluetooth.lowenergy.GattCharacteristicUIModel

@Composable
fun DeviceLEScreen(
    viewModel: BluetoothLEViewModel = hiltViewModel(),
    mac: String = "sin mac"
) {

    viewModel.connect(mac)

    Scaffold(

        topBar = { ToolbarDevice(viewModel = viewModel) },

        content = {
            ContentDevice(viewModel = viewModel) {
                viewModel.send(it)
            }
        }

    )

}


@Composable
fun ToolbarDevice(
    viewModel: BluetoothViewModel = hiltViewModel()
) {

    val isConnected = viewModel.isConnected.value

    TopAppBar(title = {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        )
        {

            Text(
                text = viewModel.getDeviceName(), modifier = Modifier.padding(12.dp)
            )

            Image(
                painterResource(
                    id = if (isConnected) {
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
                            if (isConnected) {
                                viewModel.disconnect()
                            } else {
                                viewModel.connect(viewModel.getDeviceMac())
                            }
                        }),
                colorFilter = ColorFilter.tint(color = Color.White)
            )
        }
    })
}

@Composable
fun ContentDevice(
    viewModel: BluetoothViewModel = hiltViewModel(),
    onClick: (message: String) -> Unit,
) {

    val gattServices = viewModel.services.value

    LazyColumn(modifier = Modifier.padding(4.dp)) {

        item {
            Button(onClick = {
                onClick("Pulsador 1  ")
            }) {
                Text(text = "Pulsador 1")
            }
        }

        item {
            gattServices.forEach {

                ShowGattService(it)
            }
        }
    }
}


@Composable
fun ShowGattService(gattServiceUIModel: GattServiceUIModel = GattServiceUIModel.mock()) {

    Card(
        modifier = Modifier
            .padding(4.dp)
            .background(MaterialTheme.colors.surface)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Service UUID: ",
                Modifier.padding(5.dp)
            )

            AutoResizeText(
                text = gattServiceUIModel.uuid,
                maxLines = 1,
                fontSizeRange = FontSizeRange(
                    min = 10.sp,
                    max = 40.sp,
                ),
                modifier = Modifier.padding(5.dp)
            )
            gattServiceUIModel.characteristics.forEach {
                ShowGattCGattCharacteristic(it)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ShowGattCGattCharacteristic(
    gattCharacteristicUIModel: GattCharacteristicUIModel = GattCharacteristicUIModel.mock()
) {

    Column {
        Row {

            Text(
                text = "characteristic:",
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
            )

            Text(
                text = gattCharacteristicUIModel.uuid,
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            Text(
                text = "WRITE",
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(5.dp),
                color = if (gattCharacteristicUIModel.propertyWrite) {
                    Color.Red
                } else {
                    Color.Gray
                }
            )

            Text(
                text = "READ",
                fontSize = 17.sp,
                modifier = Modifier.padding(5.dp),
                color = if (gattCharacteristicUIModel.propertyRead) {
                    Color.Red
                } else {
                    Color.Gray
                }
            )

            Text(
                text = "INDICATE",
                fontSize = 17.sp,
                modifier = Modifier.padding(5.dp),
                color = if (gattCharacteristicUIModel.propertyIndicate) {
                    Color.Red
                } else {
                    Color.Gray
                }
            )

            Text(
                text = "NOTIFY",
                fontSize = 17.sp,
                modifier = Modifier.padding(5.dp),
                color = if (gattCharacteristicUIModel.propertyNotify) {
                    Color.Red
                } else {
                    Color.Gray
                }
            )

        }
    }

}

package com.a6.bluetoothservice.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showSystemUi = true)
@Preview(showBackground = true)
@Composable
fun ShowDevice(mac:String = "sin mac", name: String = "sin nombre") {

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
package com.a6.bluetoothservice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.a6.bluetoothservice.bluetooth.classic.BluetoothClassicViewModel
import com.a6.bluetoothservice.ui.theme.BluetoothServiceTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@AndroidEntryPoint
class BluetoothClassicActivity : ComponentActivity() {

    private val viewModel: BluetoothClassicViewModel by viewModels()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mac = intent.getStringExtra("MAC")

        if (mac.isNullOrEmpty()) {
            Log.e(TAG, "BluetoothClassicActivity without mac")
            finish()
        } else {
            viewModel.connect(mac)
        }

        setContent {
            BluetoothServiceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    companion object {
        const val TAG = "TAGGG_BluetoothClassic"
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BluetoothServiceTheme {
        Greeting("Android")
    }
}
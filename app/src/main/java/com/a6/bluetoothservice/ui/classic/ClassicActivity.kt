package com.a6.bluetoothservice.ui.classic

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.a6.bluetoothservice.bluetooth.classic.BluetoothClassicViewModel
import com.a6.bluetoothservice.ui.theme.BluetoothServiceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClassicActivity : ComponentActivity() {

    private val viewModel: BluetoothClassicViewModel by viewModels()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mac = intent.getStringExtra("MAC")

        if ( mac.isNullOrEmpty()) {
            Log.e(TAG, "ClassicActivity without mac")
            finish()
        } else {
            viewModel.connect(mac)
        }

        setContent {
            BluetoothServiceTheme {
                ShowClassicDevice(viewModel)
            }
        }

    }

    override fun onDestroy() {
        viewModel.disconnect()
        super.onDestroy()
    }

    companion object {
        const val TAG = "TAGGG_BluetoothClassic"
    }
}






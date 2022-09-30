package com.a6.bluetoothservice.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.a6.bluetoothservice.bluetooth.BluetoothAndroidViewModel

class BluetoothReceiver(private val viewModel: BluetoothAndroidViewModel) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {

            BluetoothAdapter.ACTION_STATE_CHANGED -> {

                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {

                    BluetoothAdapter.STATE_OFF -> {
                        viewModel.isBluetoothOn.value = false
                    }

                    BluetoothAdapter.STATE_ON -> {
                        viewModel.isBluetoothOn.value = true
                    }

                }
            }

        }
    }
}
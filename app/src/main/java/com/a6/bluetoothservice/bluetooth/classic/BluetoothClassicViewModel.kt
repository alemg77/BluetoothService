package com.a6.bluetoothservice.bluetooth.classic

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.a6.bluetoothservice.bluetooth.BluetoothViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
@SuppressLint("MissingPermission")
class BluetoothClassicViewModel @Inject constructor(private val app: Application) :
    BluetoothViewModel(app) {

    fun connect(mac:String) {

        val bluetoothDevice = getBluetoothDevice(mac) ?: return

        val uuid = bluetoothDevice.uuids[0].uuid

        val socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)

        viewModelScope.launch(Dispatchers.IO) {

            socket.connect()

            Log.d(TAG, "Connected with ${bluetoothDevice.name}")

            val outputStream = socket.outputStream

            val tx = "Hola desde android"

            outputStream.write(tx.toByteArray())

        }

    }

    companion object {
        const val TAG = "TAGGG_BluetoothClassicViewModel"
    }


}

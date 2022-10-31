package com.a6.bluetoothservice.bluetooth.classic

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.a6.bluetoothservice.bluetooth.BluetoothViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import javax.inject.Inject


@HiltViewModel
@SuppressLint("MissingPermission")
class BluetoothClassicViewModel @Inject constructor(private val app: Application) :
    BluetoothViewModel(app) {

    private lateinit var connectionJob: Job

    private lateinit var socket: BluetoothSocket

    private lateinit var outputStream: OutputStream

    override fun connect(deviceMac: String) {

        val bluetoothDevice = getBluetoothDevice(deviceMac) ?: return

        val uuid = bluetoothDevice.uuids[0].uuid

        socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)

        connectionJob = viewModelScope.launch(Dispatchers.IO) {

            socket.connect()

            Log.d(TAG, "Connected with ${bluetoothDevice.name}")

            outputStream = socket.outputStream

            val inputStream = socket.inputStream

            val tx = "Hi! I'm a Android device"

            outputStream.write(tx.toByteArray())

            isConnected.value = true

            var numBytes: Int // bytes returned from read()

            val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

            while (true) {

                numBytes = try {
                    inputStream.read(mmBuffer)
                } catch (e: IOException) {
                    isConnected.value = false
                    break
                }

                val str = String(mmBuffer, 0, numBytes, StandardCharsets.UTF_8)

                Log.d(TAG, "Llegaron $numBytes bytes: $str")

            }

        }

    }

    fun send(data: String) {

        if (!this::outputStream.isInitialized) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            outputStream.write(data.toByteArray())
        }

    }

    override fun disconnect() {

        if (this::connectionJob.isInitialized) {

            connectionJob.cancel()

            socket.close()

        }

    }


    companion object {
        const val TAG = "TAGGG_BluetoothClassicViewModel"
    }


}

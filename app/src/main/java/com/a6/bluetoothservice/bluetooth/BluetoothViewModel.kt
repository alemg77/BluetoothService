package com.a6.bluetoothservice.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")
open class BluetoothViewModel @Inject constructor(private val app: Application) :
    AndroidViewModel(app) {

    protected var adapter: BluetoothAdapter

    val isBluetoothOn: MutableState<Boolean> = mutableStateOf(false)

    val isGattConnected: MutableState<Boolean> = mutableStateOf(false)

    val services: MutableState<List<GattServiceUIModel>> = mutableStateOf(emptyList())

    var bluetoothDevices = emptyList<BluetoothDeviceUIModel>()


    inner class BluetoothReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {

                BluetoothAdapter.ACTION_STATE_CHANGED -> {

                    when (intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )) {

                        BluetoothAdapter.STATE_OFF -> {
                            isBluetoothOn.value = false
                        }

                        BluetoothAdapter.STATE_ON -> {
                            getBondedDevices()
                            isBluetoothOn.value = true
                        }

                    }
                }

            }
        }
    }

    private val bluetoothReceiver = BluetoothReceiver()

    init {

        val bluetoothManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        adapter = bluetoothManager.adapter

        if (adapter.isEnabled) {

            getBondedDevices()

            isBluetoothOn.value = true

        }

        startReceiver()

    }

    override fun onCleared() {

        Log.d(TAG, "********* BluetoothLEViewModel onCleared ***************")

        unregisterReceiver()

        super.onCleared()
    }

    private fun startReceiver() {
        // Para enterarme cuando se activo y desactivo el Bluetooth desde el sistema
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        app.registerReceiver(bluetoothReceiver, filter)
    }

    fun unregisterReceiver() {
        Log.d(TAG, "unregisterReceiver")
        app.unregisterReceiver(bluetoothReceiver)
    }

    fun getBondedDevices(): ArrayList<BluetoothDeviceUIModel> {

        val devices = ArrayList<BluetoothDeviceUIModel>()

        if (adapter.isEnabled) {

            adapter.bondedDevices.forEach {
                devices.add(BluetoothDeviceUIModel(it.name, it.address))
            }

        }

        bluetoothDevices = devices

        return devices

    }

    companion object {
        private const val TAG = "TAGGG_BluetoothViewModel"
    }

}
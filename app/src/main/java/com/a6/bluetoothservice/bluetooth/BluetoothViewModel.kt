package com.a6.bluetoothservice.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

@SuppressLint("MissingPermission")
abstract class BluetoothViewModel constructor(private val app: Application) :
    AndroidViewModel(app) {

    private var adapter: BluetoothAdapter

    private var device: BluetoothDevice? = null

    val isConnected: MutableState<Boolean> = mutableStateOf(false)

    abstract fun connect(mac: String)

    abstract fun disconnect()

    val isBluetoothOn: MutableState<Boolean> = mutableStateOf(false)

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

            // DEVICE_TYPE_CLASSIC

            adapter.bondedDevices.forEach {
                devices.add(BluetoothDeviceUIModel(it.name, it.address, it.type))
            }

        }

        bluetoothDevices = devices

        return devices

    }

    fun getBluetoothDevice(mac: String): BluetoothDevice? {

        if (!adapter.isEnabled)
            return null

        device = adapter.bondedDevices.find { it.address == mac }

        return device

    }

    fun getDevice(): BluetoothDevice? {
        return device
    }

    fun getDeviceMac(): String {
        device?.let {
            return it.address
        }
        return "disconnected"
    }

    fun getDeviceName(): String {
        device?.let {
            return it.name
        }
        return "disconnected"
    }

    companion object {
        private const val TAG = "TAGGG_BluetoothViewModel"
    }

}
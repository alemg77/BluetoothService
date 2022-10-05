package com.a6.bluetoothservice.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
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
class BluetoothAndroidViewModel @Inject constructor(private val app: Application) :
    AndroidViewModel(app) {

    private var adapter: BluetoothAdapter

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

        Log.d(TAG, "********* BluetoothAndroidViewModel onCleared ***************")

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

    private var bluetoothGatt: BluetoothGatt? = null

    var gattService: List<BluetoothGattService>? = null

    private var mWriteCharacteristic: BluetoothGattCharacteristic? = null

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.d(TAG, "onCharacteristicChanged")
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            Log.d(TAG, "onCharacteristicRead")
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.d(TAG, "onCharacteristicWrite status: $status")
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.d(TAG, "onConnectionStateChange")
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                isGattConnected.value = true

                bluetoothGatt!!.discoverServices()

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                isGattConnected.value = false

                Log.i(TAG, "Disconnected from GATT server.")

            }
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            super.onDescriptorRead(gatt, descriptor, status)
            Log.d(TAG, "onDescriptorRead")
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            Log.d(TAG, "onDescriptorWrite")
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.d(TAG, "onMtuChanged")
        }

        override fun onPhyRead(gatt: BluetoothGatt, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(gatt, txPhy, rxPhy, status)
            Log.d(TAG, "onPhyRead")
        }

        override fun onPhyUpdate(gatt: BluetoothGatt, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status)
            Log.d(TAG, "onPhyUpdate")
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            Log.d(TAG, "onReadRemoteRssi")
        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
            super.onReliableWriteCompleted(gatt, status)
            Log.d(TAG, "onReliableWriteCompleted")
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onServicesDiscovered GATT_SUCCESS ")
                gattService = gatt.services

                displayGattServices(gattService)

                services.value = mapper(gattService)

            } else {
                Log.d(
                    TAG,
                    "onServicesDiscovered received: $status"
                )
            }
        }
    }

    private fun mapper(gattServices: List<BluetoothGattService>?): List<GattServiceUIModel> {

        val services = mutableListOf<GattServiceUIModel>()

        gattServices?.forEach { service ->

            val listCharacteristics = mutableListOf<GattCharacteristicUIModel>()

            service.characteristics.forEach { characteristic ->
                listCharacteristics.add(
                    GattCharacteristicUIModel.build(
                        characteristic.uuid.toString(),
                        characteristic.properties
                    )
                )
            }

            services.add(
                GattServiceUIModel(
                    service.uuid.toString(),
                    listCharacteristics
                )
            )
        }

        return services

    }

    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {

        if (gattServices == null) return

        for (gattService in gattServices) {

            val uuid = gattService.uuid.toString()
            Log.d(TAG, "gattService: $uuid")

            val gattCharacteristics = gattService.characteristics
            for (gattCharacteristic in gattCharacteristics) {

                val uuid1 = gattCharacteristic.uuid

                val properties = gattCharacteristic.properties
                Log.d(TAG, "    gattCharacteristic: $uuid1")

                if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0x0) {
                    Log.d(TAG, "        PROPERTY_WRITE")
                    mWriteCharacteristic = gattCharacteristic
                }

                if (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0x0) {
                    Log.d(TAG, "        PROPERTY_NOTIFY")
                }

                if (properties and BluetoothGattCharacteristic.PERMISSION_READ != 0x0) {
                    Log.d(TAG, "        PERMISSION_READ")
                }

                if (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0x0) {
                    Log.d(TAG, "        PROPERTY_INDICATE")
                }

            }
        }
    }

    fun connectGatt(mac: String): Boolean {

        if (!adapter.isEnabled)
            return false

        val device = adapter.bondedDevices.find { it.address == mac } ?: return false

        bluetoothGatt = device.connectGatt(app, false, gattCallback)

        return true
    }

    fun disConnectGatt() {
        bluetoothGatt?.disconnect()
    }

    fun send(string: String): Boolean {

        if (!isGattConnected.value) {
            return false
        }

        if (mWriteCharacteristic == null) {
            return false
        }

        mWriteCharacteristic?.value = string.toByteArray()

        bluetoothGatt?.writeCharacteristic(mWriteCharacteristic)

        return true

    }

    companion object {
        private const val TAG = "TAGGG_BluetoothLeService"
        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTED = 2
        const val STATE_SERVICES_DISCOVERED = 3
    }

}
package com.a6.bluetoothservice.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


@Suppress("DEPRECATION")
@SuppressLint("MissingPermission")
class BluetoothLeService : Service() {

    init {
        Log.d(TAG, "BluetoothLeService running.... ")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BluetoothLeService onCreate() ")
    }

    override fun onDestroy() {
        Log.d(TAG, "BluetoothLeService onDestroy() ")
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private var bluetoothGatt: BluetoothGatt? = null

    private var connectionState = STATE_DISCONNECTED

    var serviciosGatt: List<BluetoothGattService>? = null
        private set

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

                sendStateToActivity(STATE_CONNECTED)

                connectionState = STATE_CONNECTED

                bluetoothGatt!!.discoverServices()

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED
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
                serviciosGatt = gatt.services

                displayGattServices(serviciosGatt)

                sendStateToActivity(STATE_SERVICES_DISCOVERED)

            } else {
                Log.d(
                    TAG,
                    "onServicesDiscovered received: $status"
                )
            }
        }
    }

    private var mWriteCharacteristic: BluetoothGattCharacteristic? = null

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

    fun conectarGatt(device: BluetoothDevice) {
        Log.d(TAG, "Iniciando GATT")
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    fun enviar(byteArray: ByteArray): Boolean {

        if (connectionState == STATE_DISCONNECTED) {
            return false
        }

        if (mWriteCharacteristic == null) {
            return false
        }

        mWriteCharacteristic?.value = byteArray

        bluetoothGatt?.writeCharacteristic(mWriteCharacteristic)

        return true

    }

    private val _bluetoothState = MutableLiveData<Int>()
    val bluetoothStatus: LiveData<Int> = _bluetoothState

    private fun sendStateToActivity(data: Int) {
        _bluetoothState.postValue(data)
    }



    companion object {
        private const val TAG = "TAGGG_BluetoothLeService"
        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTED = 2
        const val STATE_SERVICES_DISCOVERED = 3
    }

}
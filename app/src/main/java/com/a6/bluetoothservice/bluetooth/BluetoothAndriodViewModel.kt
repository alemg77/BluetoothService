package com.a6.bluetoothservice.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class BluetoothAndroidViewModel(private val app: Application) : AndroidViewModel(app) {

    private lateinit var adapter: BluetoothAdapter

    var isBluetoothOn: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    var bluetoothDevices : MutableLiveData<Set<BluetoothDevice>?> = MutableLiveData<Set<BluetoothDevice>?>()

    init {

        val bluetoothManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        adapter = bluetoothManager.adapter

        bluetoothDevices.value = null

        isBluetoothOn.value = adapter.isEnabled

    }

    @SuppressLint("MissingPermission")
    fun getBondedDevices(): Set<BluetoothDevice>? {

        if (adapter.isEnabled) {
            bluetoothDevices.value = adapter.bondedDevices
            return adapter.bondedDevices
        }

        return null

    }


}
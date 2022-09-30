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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BluetoothAndroidViewModel(private val app: Application) : AndroidViewModel(app) {

    private var adapter: BluetoothAdapter

    // TODO: ARREGLAR ESTE LEAK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //private var mBluetoothLeService: BluetoothLeService

    private val _isBluetoothOn = MutableLiveData<Boolean>()
    val isBluetoothOn: LiveData<Boolean> = _isBluetoothOn

    private val _bluetoothDevices = MutableLiveData<ArrayList<BluetoothDeviceUIModel>>()
    val bluetoothDevices: LiveData<ArrayList<BluetoothDeviceUIModel>> = _bluetoothDevices

    inner class BluetoothReceiver() : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {

                BluetoothAdapter.ACTION_STATE_CHANGED -> {

                    when (intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )) {

                        BluetoothAdapter.STATE_OFF -> {
                            _isBluetoothOn.postValue(false)
                        }

                        BluetoothAdapter.STATE_ON -> {
                            _isBluetoothOn.postValue(true)
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

        _bluetoothDevices.postValue(null)

        _isBluetoothOn.postValue(adapter.isEnabled)

        startReceiver()

        /*
        Intent(this, BluetoothLeService::class.java).also {
            startService(it)
        }
        */

        /*
        Intent(this, BluetoothLeService::class.java).also {
            stopService(it)
        }
         */

    }

    override fun onCleared() {

        Log.d("TAGGG", "********* BluetoothAndroidViewModel onCleared ***************")

        stopReceiver()

        super.onCleared()
    }

    private fun startReceiver() {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        app.registerReceiver(bluetoothReceiver, filter)
    }

    private fun stopReceiver() {
        app.unregisterReceiver(bluetoothReceiver)
    }

    @SuppressLint("MissingPermission")
    fun getBondedDevices(): ArrayList<BluetoothDeviceUIModel> {

        val devices = ArrayList<BluetoothDeviceUIModel>()

        if (adapter.isEnabled) {

            adapter.bondedDevices.forEach {
                devices.add(BluetoothDeviceUIModel(it.name, it.address))
            }

        }

        _bluetoothDevices.postValue(devices)

        return devices

    }

    /*
    fun connect(device: BluetoothDevice) {
        mBluetoothLeService.conectarGatt(device)
    }

     */


}
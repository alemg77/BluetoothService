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
class BluetoothAndroidViewModel @Inject constructor(private val app: Application) :
    AndroidViewModel(app) {

    private var adapter: BluetoothAdapter

    // TODO: ARREGLAR ESTE LEAK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //private var mBluetoothLeService: BluetoothLeService

    val isBluetoothOn: MutableState<Boolean> = mutableStateOf(false)

    var bluetoothDevices = emptyList<BluetoothDeviceUIModel>()

    inner class BluetoothReceiver() : BroadcastReceiver() {

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
    fun getBondedDevices() {

        val devices = ArrayList<BluetoothDeviceUIModel>()

        if (adapter.isEnabled) {

            adapter.bondedDevices.forEach {
                devices.add(BluetoothDeviceUIModel(it.name, it.address))
            }

        }

        bluetoothDevices = devices

    }

    /*
    fun connect(device: BluetoothDevice) {
        mBluetoothLeService.conectarGatt(device)
    }

     */


}
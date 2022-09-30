package com.a6.bluetoothservice

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.a6.bluetoothservice.bluetooth.BluetoothAndroidViewModel
import com.a6.bluetoothservice.bluetooth.BluetoothLeService
import com.a6.bluetoothservice.bluetooth.BluetoothReceiver
import com.a6.bluetoothservice.screens.ShowDevices

class MainActivity : AppCompatActivity() {

    private val mBluetoothLeService = BluetoothLeService()

    private lateinit var bluetoothReceiver: BluetoothReceiver

    private lateinit var viewModel: BluetoothAndroidViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Inicio de la app BluetoothService")

        viewModel = ViewModelProvider(this)[BluetoothAndroidViewModel::class.java]

        bluetoothReceiver = BluetoothReceiver(viewModel)

        // Este avisa cuando se activa y desactiva el bluetooth desde el sistema
        startBroadcastReceiverBluetoothAdapter()

        initialSetupBluetooth()

        setContent {
            ShowDevices(viewModel)
        }

    }

    override fun onDestroy() {

        Intent(this, BluetoothLeService::class.java).also {
            stopService(it)
        }

        stopBroadcastReceiverBluetoothAdapter()

        super.onDestroy()
    }

    private fun initialSetupBluetooth() {

        Intent(this, BluetoothLeService::class.java).also {
            startService(it)
        }

        receiveDataFromBluetoothLeService()

        if (checkMultiplesPermissions(PERMISSIONS)) {
            if (checkBluetoothEnable()) {
                viewModel.getBondedDevices()
            }
        } else {
            askForMultiplesPermissions(PERMISSIONS)
        }

    }

    private fun receiveDataFromBluetoothLeService() {
        mBluetoothLeService.bluetoothStatus.observe(this) {

            when (it) {
                BluetoothLeService.STATE_CONNECTED -> {
                    Log.d(TAG, "El servicio avisa que se conecto ")
                }
                BluetoothLeService.STATE_SERVICES_DISCOVERED -> {
                    Log.d(TAG, "Listo para funcionar!!!!!!!!!!!!!!!!!!!!!!")
                    val t = "pulsador 1  "
                    mBluetoothLeService.enviar(t.toByteArray())
                }
            }

        }
    }

    private fun startBroadcastReceiverBluetoothAdapter() {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        this.registerReceiver(bluetoothReceiver, filter)
    }

    private fun stopBroadcastReceiverBluetoothAdapter() {
        unregisterReceiver(bluetoothReceiver)
    }

    private fun checkMultiplesPermissions(permissions: Array<String>): Boolean {

        for (permission in permissions) {
            if (!checkPermission(permission)) {
                return false
            }
        }
        return true

    }

    private fun checkPermission(permission: String): Boolean {

        if (ActivityCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    private fun askForMultiplesPermissions(permissions: Array<String>) {

        val per = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
            for (permission in it) {
                if (!permission.value) {
                    Log.d(TAG, "Nos rechazaron: $permission")
                    return@registerForActivityResult
                }
            }
            checkBluetoothEnable()
        }

        per.launch(permissions)

    }

    private fun checkBluetoothEnable(): Boolean {

        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        if (!bluetoothManager.adapter.isEnabled) {

            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            val resultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                    when (result.resultCode) {
                        Activity.RESULT_OK -> {
                            viewModel.getBondedDevices()
                        }
                        Activity.RESULT_CANCELED -> {
                            // TODO: Explicarle al usuario que es necesario bluetooth
                        }
                    }
                }

            resultLauncher.launch(enableBtIntent)
            return false

        } else {
            return true
        }
    }

    companion object {
        const val TAG = "TAGGG_MainActivity"

        var PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }

    }
}


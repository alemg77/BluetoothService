package com.a6.bluetoothservice

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private val mBluetoothLeService = BluetoothLeService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Inicio de la app BluetoothService")


        setContent {
            Hello()
        }

        initialSetupBluetooth()

    }

    private fun initialSetupBluetooth() {


        Intent(this, BluetoothLeService::class.java).also {
            startService(it)
        }

        startBroadcastReceiverBluetoothAdapter()

        receiveDataFromBluetoothLeService()

        if (checkMultiplesPermissions(PERMISSIONS)) {
            if (checkBluetoothEnable()) {
                checkBondedDevices()
            }
        } else {
            askForMultiplesPermissions(PERMISSIONS)
        }

    }

    private fun receiveDataFromBluetoothLeService() {
        mBluetoothLeService.notifications.observe(this) {

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

    override fun onDestroy() {

        Intent(this, BluetoothLeService::class.java).also {
            stopService(it)
        }

        stopBroadcastReceiverBluetoothAdapter()

        super.onDestroy()
    }

    private fun startBroadcastReceiverBluetoothAdapter() {
        IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED).also {
            registerReceiver(broadcastReceiverBluetoothAdapter, it)
        }
    }

    private fun stopBroadcastReceiverBluetoothAdapter() {
        unregisterReceiver(broadcastReceiverBluetoothAdapter)
    }

    private val broadcastReceiverBluetoothAdapter: BroadcastReceiver =
        object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action

                // When discovery finds a device
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )) {

                        BluetoothAdapter.STATE_OFF -> {
                            Log.d(TAG, "onReceive: STATE OFF")
                        }

                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF")
                        }

                        BluetoothAdapter.STATE_ON -> {
                            Log.d(TAG, "mBroadcastReceiver1: STATE ON")
                        }

                        BluetoothAdapter.STATE_TURNING_ON -> {
                            Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON")
                        }
                    }
                }
            }
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
                    if (result.resultCode == Activity.RESULT_OK) {
                        //  val data: Intent? = result.data

                        // TODO: Verificar que el usuario acepto activar bluetooth

                        checkBondedDevices()

                    }
                }

            resultLauncher.launch(enableBtIntent)

            return false

        } else {
            return true
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkBondedDevices() {

        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        val adapter = bluetoothManager.adapter

        val pairedDevices: Set<BluetoothDevice> = adapter.bondedDevices

        pairedDevices.forEach { device ->

            val deviceName = device.name

            val deviceHardwareAddress = device.address // MAC address

            Log.d(TAG, " $deviceName  $deviceHardwareAddress ")

            if (deviceName.equals("Wireless Controller")) {

                Log.d(TAG, "Encontramos un dispocitivo Wireless Controller")

                mBluetoothLeService.conectarGatt(device)

            }

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

@Composable
private fun Hello() {
    Text("Hola Jetpack Componse")
}
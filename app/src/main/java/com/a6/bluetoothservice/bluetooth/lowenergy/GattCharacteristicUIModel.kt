package com.a6.bluetoothservice.bluetooth.lowenergy

import android.bluetooth.BluetoothGattCharacteristic

data class GattCharacteristicUIModel(
    val uuid: String,
    val propertyWrite: Boolean,
    val propertyRead: Boolean,
    val propertyNotify: Boolean,
    val propertyIndicate: Boolean
) {

    companion object {

        fun build(uuid: String, properties: Int): GattCharacteristicUIModel {
            return GattCharacteristicUIModel(
                uuid,
                (properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0x0),
                (properties and BluetoothGattCharacteristic.PERMISSION_READ != 0x0),
                (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0x0),
                (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0x0),
            )
        }

        fun mock(): GattCharacteristicUIModel {
            return GattCharacteristicUIModel(
                "00000-1111-22222-3333333-444444444-55555",
                propertyWrite = true,
                propertyRead = true,
                propertyNotify = false,
                propertyIndicate = false
            )
        }

    }
}

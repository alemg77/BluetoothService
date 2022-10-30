package com.a6.bluetoothservice.bluetooth

import com.a6.bluetoothservice.bluetooth.lowenergy.GattCharacteristicUIModel

class GattServiceUIModel(
    val uuid: String,
    val characteristics: List<GattCharacteristicUIModel>
){

    companion object {

        fun mock(): GattServiceUIModel {
            return GattServiceUIModel(
                uuid = "999999-99999-999999",
                characteristics = listOf(GattCharacteristicUIModel.mock())
            )

        }
    }
}

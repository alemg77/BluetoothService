package com.a6.bluetoothservice.bluetooth

data class BluetoothDeviceUIModel(
    val name:String,
    val mac:String,
    val type:Int
) {
    companion object {


        val MOCK_DEVICE1 = BluetoothDeviceUIModel("uno", "00:11:11:11", 1)
        private val mock_device2 = BluetoothDeviceUIModel("dos", "00:22:22:22", 2)
        private val mock_device3 = BluetoothDeviceUIModel("tres", "00:33:33:33", 3)

        val MOCK_DEVICES: ArrayList<BluetoothDeviceUIModel> = arrayListOf(
            MOCK_DEVICE1,
            mock_device2,
            mock_device3
        )

    }
}

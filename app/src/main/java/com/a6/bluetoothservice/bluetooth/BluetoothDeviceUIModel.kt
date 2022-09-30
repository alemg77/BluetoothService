package com.a6.bluetoothservice.bluetooth

data class BluetoothDeviceUIModel(
    val name:String,
    val mac:String
) {
    companion object {


        val MOCK_DEVICE1 = BluetoothDeviceUIModel("uno", "00:11:11:11")
        private val mock_device2 = BluetoothDeviceUIModel("dos", "00:22:22:22")
        private val mock_device3 = BluetoothDeviceUIModel("tres", "00:33:33:33")

        val MOCK_DEVICES: ArrayList<BluetoothDeviceUIModel> = arrayListOf(
            MOCK_DEVICE1,
            mock_device2,
            mock_device3
        )

    }
}

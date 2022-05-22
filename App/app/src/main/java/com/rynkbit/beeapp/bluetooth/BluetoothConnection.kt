package com.rynkbit.beeapp.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

class BluetoothConnection private constructor() {
    companion object {
        private val instance = BluetoothConnection()
        operator fun invoke(): BluetoothConnection {
            return instance
        }
    }

    var bluetoothGatt: BluetoothGatt? = null
    var bluetoothDevice: BluetoothDevice? = null
}

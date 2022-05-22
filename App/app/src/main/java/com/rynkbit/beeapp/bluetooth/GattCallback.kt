package com.rynkbit.beeapp.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor


class GattCallback private constructor(): BluetoothGattCallback() {
    val onConnectionChanged: MutableList<(gatt: BluetoothGatt?, status: Int, newState: Int) -> Unit> = mutableListOf()
    val onServiceDiscovered: MutableList<(gatt: BluetoothGatt?, status: Int) -> Unit> = mutableListOf()
    val onCharacteristicRead: MutableList<(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) -> Unit> = mutableListOf()
    val onDescriptorRead: MutableList<(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) -> Unit> = mutableListOf()
    val onCharacteristicChanged: MutableList<(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) -> Unit> = mutableListOf()
    val onServiceChanged: MutableList<(gatt: BluetoothGatt) -> Unit> = mutableListOf()

    companion object {
        private val instance = GattCallback()
        operator fun invoke(): GattCallback {
            return instance
        }
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        onConnectionChanged.forEach { observer ->
            observer(gatt, status, newState)
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)
        onServiceDiscovered.forEach { observer ->
            observer(gatt, status)
        }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicRead(gatt, characteristic, status)
        onCharacteristicRead.forEach { observer ->
            observer(gatt, characteristic, status)
        }
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorRead(gatt, descriptor, status)
        onDescriptorRead.forEach { observer ->
            observer(gatt, descriptor, status)
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        super.onCharacteristicChanged(gatt, characteristic)
        onCharacteristicChanged.forEach { observer ->
            observer(gatt, characteristic)
        }
    }

    override fun onServiceChanged(gatt: BluetoothGatt) {
        super.onServiceChanged(gatt)
        onServiceChanged.forEach { observer ->
            observer(gatt)
        }
    }
}
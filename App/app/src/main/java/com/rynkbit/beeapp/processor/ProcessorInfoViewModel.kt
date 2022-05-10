package com.rynkbit.beeapp.processor

import android.annotation.SuppressLint
import android.bluetooth.*
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProcessorInfoViewModel : ViewModel() {
    val services: LiveData<List<BluetoothGattService>>
        get() = _services

    private val _services: MutableLiveData<List<BluetoothGattService>> = MutableLiveData()

    @SuppressLint("MissingPermission")
    fun onServiceDiscovered(gatt: BluetoothGatt?, status: Int) {
        gatt?.services?.forEach {
            it.characteristics.forEach { characteristic ->
                if (characteristic.uuid.toString() == "00002a1c-0000-1000-8000-00805f9b34fb") {
                    gatt.setCharacteristicNotification(characteristic, true)
                    gatt.readCharacteristic(characteristic)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        if (gatt != null && characteristic != null) {
            gatt.readCharacteristic(characteristic)
            _services.postValue(gatt.services)
        }
    }

    @SuppressLint("MissingPermission")
    fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        if (gatt != null && descriptor != null) {
            _services.postValue(gatt.services)
        }
    }
}
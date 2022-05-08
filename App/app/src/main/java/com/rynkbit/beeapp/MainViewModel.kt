package com.rynkbit.beeapp

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val bluetoothDevices: LiveData<List<BluetoothDevice>>
        get() = _bluetoothDevices

    private val _bluetoothDevices = MutableLiveData<List<BluetoothDevice>>()

    fun onScanResult(devices: List<BluetoothDevice>) {
        _bluetoothDevices.value = devices
    }
}
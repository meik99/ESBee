package com.rynkbit.beeapp.processor

import android.annotation.SuppressLint
import android.bluetooth.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rynkbit.beeapp.bluetooth.BluetoothConnection

const val uuidPumpControl = "00002a9f-0000-1000-8000-00805f9b34fb"
const val uuidTemperature = "00002a1c-0000-1000-8000-00805f9b34fb"
const val uuidBeeService = "0000181a-0000-1000-8000-00805f9b34fb"

class ProcessorInfoViewModel : ViewModel() {
    val temperature: LiveData<String>
        get() = _temperature

    private val _temperature: MutableLiveData<String> = MutableLiveData()

    val pumpState: LiveData<String>
        get() = _pumpState

    private val _pumpState: MutableLiveData<String> = MutableLiveData("OFF")

    val pumpEnabled: LiveData<Boolean>
        get() = _pumpEnabled

    private val _pumpEnabled: MutableLiveData<Boolean> = MutableLiveData(false)

    private var pumpCharacteristic: BluetoothGattCharacteristic? = null

    @SuppressLint("MissingPermission")
    fun onServiceDiscovered(gatt: BluetoothGatt?, status: Int) {
        gatt?.services?.forEach {
            it.characteristics.forEach { characteristic ->
                if (characteristic.uuid.toString() == uuidTemperature) {
                    gatt.setCharacteristicNotification(characteristic, true)
                    gatt.readCharacteristic(characteristic)
                }

                if (characteristic.uuid.toString() == uuidPumpControl) {
                    gatt.setCharacteristicNotification(characteristic, true)
                    gatt.readCharacteristic(characteristic)
                    pumpCharacteristic = characteristic
                    _pumpEnabled.postValue(true)
                }
            }
        }
    }

    fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        updateCharacteristicValues(gatt, characteristic)
    }

    fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        updateCharacteristicValues(gatt, characteristic)
    }

    private fun updateCharacteristicValues(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        if (gatt != null && characteristic != null) {
            when (characteristic.uuid.toString()) {
                uuidTemperature -> {
                    _temperature.postValue(characteristic.value.decodeToString())
                }
                uuidPumpControl -> {
                    _pumpState.postValue(characteristic.value.decodeToString())
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun writePumpControl(text: String) {
        BluetoothConnection().bluetoothGatt?.let { gatt ->
            pumpCharacteristic?.let { characteristic ->
                characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                characteristic.value = text.encodeToByteArray()
                gatt.writeCharacteristic(characteristic)
            }
        }
    }

    fun togglePump() {
        val enabled = pumpEnabled.value ?: false
        val state = pumpState.value ?: "No read"

        if (!enabled) {
            return
        }

        if (state == "OFF") {
            writePumpControl("ON")
        } else {
            writePumpControl("OFF")
        }
    }
}
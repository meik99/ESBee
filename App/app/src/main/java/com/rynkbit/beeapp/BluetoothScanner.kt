package com.rynkbit.beeapp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log

class BluetoothScanner(private val bluetoothManager: BluetoothManager?) : ScanCallback() {
    var bluetoothDeviceObserver: (bluetoothDevices: List<BluetoothDevice>) -> Unit = fun(_: List<BluetoothDevice>) {}

    private val bluetoothDeviceMap = mutableMapOf<String, BluetoothDevice>()

    @SuppressLint("MissingPermission")
    fun scan() {
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
        val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

        bluetoothLeScanner?.startScan(this)
    }

    @SuppressLint("MissingPermission")
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)

        result?.let { scanResult ->
            addDeviceFromResult(scanResult)
        }

        bluetoothDeviceObserver(bluetoothDeviceMap.values.toList())
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        super.onBatchScanResults(results)

        results?.let { result ->
            result.forEach { scanResult ->
                addDeviceFromResult(scanResult)
            }
        }

        bluetoothDeviceObserver(bluetoothDeviceMap.values.toList())
    }

    @SuppressLint("MissingPermission")
    private fun addDeviceFromResult(result: ScanResult) {
        val name = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            result.device.alias
        } else {
            result.device.name
        }

        if (name != null) {
            bluetoothDeviceMap[name] = result.device
        }
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)

        Log.e(BluetoothScanner::class.java.simpleName, "scan failed with error code $errorCode")
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        bluetoothDeviceMap.clear()

        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
        val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

        bluetoothLeScanner?.stopScan(this)
    }
}

@SuppressLint("MissingPermission")
fun BluetoothDevice.getAliasOrName(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        this.alias ?: ""
    } else {
        this.name ?: ""
    }

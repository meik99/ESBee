package com.rynkbit.beeapp

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.rynkbit.beeapp.ui.theme.BeeAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Main()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@ExperimentalPermissionsApi
@Preview(showBackground = true)
@Composable
fun Main() {
    BeeAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {

//
            val context = LocalContext.current
            val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )
            } else {
                rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )
            }
//
//
            val lifecycleOwner = LocalLifecycleOwner.current
            SideEffect {
                val eventObserver = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_START -> {
                            permissionState.launchMultiplePermissionRequest()
                        }
                        else -> {}
                    }
                }

                lifecycleOwner.lifecycle.addObserver(eventObserver)
            }

            var deviceList = mutableStateListOf<BluetoothDevice>()

            when {
                permissionState.allPermissionsGranted -> {
                    val bluetoothManager: BluetoothManager? =
                        getSystemService(context, BluetoothManager::class.java)
                    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
                    val bluetoothScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
                    val bluetoothDeviceMap = mutableMapOf<String, BluetoothDevice>()

                    bluetoothScanner?.let { bluetoothLeScanner ->
                        @SuppressLint("MissingPermission")
                        if (bluetoothAdapter.isEnabled) {
                            bluetoothLeScanner.startScan(
                                object : ScanCallback() {
                                    override fun onScanResult(
                                        callbackType: Int,
                                        result: ScanResult?
                                    ) {
                                        super.onScanResult(callbackType, result)
                                        result?.apply {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                if(this.device.alias != null) {
                                                    bluetoothDeviceMap[this.device.alias ?: ""] = this.device
                                                }
                                            } else {
                                                if(this.device.name != null) {
                                                    bluetoothDeviceMap[this.device.name] = this.device
                                                }
                                            }

                                            deviceList.clear()
                                            deviceList.addAll(bluetoothDeviceMap.values)
                                        }
                                    }
                                })
                        }
                    }
                }
                else -> {
                    Text(text = "No permissions were granted")
                }
            }

            LazyColumn {
                deviceList.forEach {
                    item {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            Text(text = it.name ?: "No name")
                            Text(text = it.address ?: "No address")
                        }
                    }
                }
            }
        }
    }
}

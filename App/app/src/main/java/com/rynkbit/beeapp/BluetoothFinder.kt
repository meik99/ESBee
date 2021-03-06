package com.rynkbit.beeapp

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.rynkbit.beeapp.bluetooth.BluetoothConnection
import com.rynkbit.beeapp.bluetooth.GattCallback
import com.rynkbit.beeapp.processor.ProcessorInfoViewModel
import com.rynkbit.beeapp.ui.theme.defaultElevation
import com.rynkbit.beeapp.ui.theme.defaultPadding
import com.rynkbit.beeapp.ui.theme.largePadding
import kotlinx.coroutines.*

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
@Composable
fun BluetoothFinder(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        PermissionRequest { permissionState ->
            val viewModel: MainViewModel = viewModel()

            if (permissionState.allPermissionsGranted) {
                val bluetoothDevices by viewModel.bluetoothDevices.observeAsState(listOf())
                val bluetoothManager = ContextCompat.getSystemService(
                    LocalContext.current, BluetoothManager::class.java
                )
                val scanner = BluetoothScanner(bluetoothManager)

                scanner.bluetoothDeviceObserver = viewModel::onScanResult

                BluetoothScanLifecycleManager(scanner)
                BluetoothDeviceList(bluetoothDevices, navController)
            }
        }
    }
}

@Composable
fun BluetoothScanLifecycleManager(scanner: BluetoothScanner) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner) {
        val lifeCycleEventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> scanner.scan()
                Lifecycle.Event.ON_PAUSE -> scanner.stop()
                else -> { /* No OP */
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifeCycleEventObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifeCycleEventObserver)
        }
    }

}

@SuppressLint("MissingPermission")
@ExperimentalMaterialApi
@Composable
fun BluetoothDeviceList(bluetoothDevices: List<BluetoothDevice>, navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Row {
        LazyColumn {
            items(bluetoothDevices) { bluetoothDevice ->
            val context = LocalContext.current
                BluetoothDeviceItem(
                    name = bluetoothDevice.getAliasOrName(),
                    address = bluetoothDevice.address,
                    onClick = {
                        GattCallback().onConnectionChanged.add(fun(
                            gatt: BluetoothGatt?,
                            status: Int,
                            newState: Int
                        ) {
                            if (newState == BluetoothProfile.STATE_CONNECTED && gatt != null) {
                                GattCallback().onConnectionChanged.clear()
                                CoroutineScope(Dispatchers.Main).launch {
                                    BluetoothConnection().bluetoothDevice = bluetoothDevice
                                    BluetoothConnection().bluetoothGatt = gatt
                                    navController.navigate("processor/info")
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Could not connect to device. Status: $status, State: $newState",
                                        duration = SnackbarDuration.Long
                                    )
                                }
                            }
                            Log.d("BluetoothFinder", "onConnectionStateChange: $status, $newState")
                        })
                        bluetoothDevice.connectGatt(context, false, GattCallback())
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.size(defaultPadding))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Scanning for devices", color = Color.LightGray)
            Spacer(modifier = Modifier.height(largePadding))
            CircularProgressIndicator(
                Modifier.size(64.dp, 64.dp)
            )
        }
    }

    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.Bottom))
}

@ExperimentalMaterialApi
@Preview
@Composable
fun BluetoothDeviceItem(
    name: String = "Device Name",
    address: String = "00:00:00:00:00:00",
    onClick: () -> Unit = fun() {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(defaultPadding), elevation = defaultElevation, onClick = onClick
    ) {
        Column {
            Text(
                text = name,
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = defaultPadding,
                    bottom = defaultPadding
                )
            )
            Text(
                text = address,
                modifier = Modifier.padding(
                    top = defaultPadding,
                    start = 16.dp,
                    end = defaultPadding,
                    bottom = 16.dp
                )
            )
        }
    }
}
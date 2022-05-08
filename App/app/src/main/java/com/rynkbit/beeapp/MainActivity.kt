package com.rynkbit.beeapp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.rynkbit.beeapp.ui.theme.BeeAppTheme
import com.rynkbit.beeapp.ui.theme.defaultPadding
import com.rynkbit.beeapp.ui.theme.largePadding

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

            PermissionRequest { permissionState ->
                val viewModel: MainViewModel = viewModel()

                if (permissionState.allPermissionsGranted) {
                    val bluetoothDevices by viewModel.bluetoothDevices.observeAsState(listOf())
                    val bluetoothManager = getSystemService(
                        LocalContext.current, BluetoothManager::class.java
                    )
                    val scanner = BluetoothScanner(bluetoothManager)

                    scanner.bluetoothDeviceObserver = viewModel::onScanResult

                    BluetoothScanLifecycleManager(scanner)
                    BluetoothDeviceList(bluetoothDevices)
                }
            }

        }
    }
}

@Composable
fun BluetoothScanLifecycleManager(scanner: BluetoothScanner) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner) {
        val lifeCycleEventObserver = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_RESUME -> scanner.scan()
                Lifecycle.Event.ON_PAUSE -> scanner.stop()
                else -> { /* No OP */ }
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifeCycleEventObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifeCycleEventObserver)
        }
    }

}

@Composable
fun BluetoothDeviceList(bluetoothDevices: List<BluetoothDevice>) {
    if (bluetoothDevices.isNotEmpty()) {
        LazyColumn {
            items(bluetoothDevices) {
                BluetoothDeviceItem(
                    name = it.getAliasOrName(),
                    address = it.address
                )
                Divider()
            }
        }
    } else {
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
    }
}

@Composable
fun BluetoothDeviceItem(name: String = "Device Name", address: String = "00:00:00:00:00:00") {
    Text(text = name, modifier = Modifier.padding(defaultPadding))
    Text(text = address, modifier = Modifier.padding(defaultPadding))
}
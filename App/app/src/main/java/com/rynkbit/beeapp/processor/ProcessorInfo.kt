package com.rynkbit.beeapp.processor

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rynkbit.beeapp.bluetooth.BluetoothConnection
import com.rynkbit.beeapp.bluetooth.GattCallback
import com.rynkbit.beeapp.ui.theme.defaultPadding
import com.rynkbit.beeapp.ui.theme.doublePadding
import java.util.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.rynkbit.beeapp.ui.theme.triplePadding

@ExperimentalFoundationApi
@SuppressLint("MissingPermission")
@Composable
fun ProcessorInfo() {
    val viewModel: ProcessorInfoViewModel = viewModel()
    val services by viewModel.services.observeAsState(listOf())
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner) {
        val eventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> BluetoothConnection().bluetoothGatt?.let { bluetoothGatt ->
                    GattCallback().onServiceDiscovered.add(viewModel::onServiceDiscovered)
                    GattCallback().onCharacteristicRead.add(viewModel::onCharacteristicRead)
                    GattCallback().onDescriptorRead.add(viewModel::onDescriptorRead)
                    bluetoothGatt.discoverServices()
                }
                Lifecycle.Event.ON_PAUSE -> BluetoothConnection().bluetoothGatt?.let { bluetoothGatt ->
                    GattCallback().onServiceDiscovered.remove(viewModel::onServiceDiscovered)
                    GattCallback().onCharacteristicRead.remove(viewModel::onCharacteristicRead)
                    GattCallback().onDescriptorRead.remove(viewModel::onDescriptorRead)
                    bluetoothGatt.disconnect()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(eventObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(eventObserver)
        }
    }

    LazyVerticalGrid(cells = GridCells.Fixed(2)) {
        item {
            TemperatureCard(services)
        }
    }
}

@Composable
fun GattServiceList(services: List<BluetoothGattService>) {
    LazyColumn {
        items(services) {
            GattService(it)
        }
    }
}

@Composable
fun TemperatureCard(services: List<BluetoothGattService>) {
    val temperature = services
        .firstOrNull {
            it.uuid.toString() == "0000181a-0000-1000-8000-00805f9b34fb"
        }
        ?.getCharacteristic(UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb"))
        ?.getStringValue(0) ?: ""
    Card(modifier = Modifier.padding(defaultPadding).fillMaxWidth(), elevation = doublePadding) {
        Column(Modifier.padding(triplePadding)) {
            Text(
                text = "Temperature",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(doublePadding))
            Text(
                text = temperature,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun GattService(
    gattService: BluetoothGattService = BluetoothGattService(
        UUID.randomUUID(), BluetoothGattService.SERVICE_TYPE_PRIMARY
    )
) {
    if (gattService.uuid.toString() == "0000181a-0000-1000-8000-00805f9b34fb") {
        Card(
            modifier = Modifier
                .padding(defaultPadding)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(doublePadding)) {
                Text(text = "UUID", color = Color.LightGray)
                Text(text = gattService.uuid.toString())
                Spacer(modifier = Modifier.height(defaultPadding))



                if (gattService.characteristics.isNotEmpty()) {
                    Text(text = "Characteristics", color = Color.LightGray)
                }

                gattService.characteristics.forEach { characteristic ->
                    Divider()
                    GattCharacteristic(characteristic)
                }

                gattService.includedServices.forEach { service ->
                    service.characteristics.forEach { characteristic ->
                        Divider()
                        GattCharacteristic(characteristic)
                    }
                }
            }
        }
    }
}

@Composable
fun GattCharacteristic(
    characteristic: BluetoothGattCharacteristic
) {
    Column {
        Text(text = "UUID", color = Color.LightGray)
        Text(text = characteristic.uuid.toString())

        if (characteristic.value != null) {
            Text(text = "Content", color = Color.LightGray)
            Text(text = characteristic.value.contentToString())
            Text(text = characteristic.value.decodeToString())
            Text(text = characteristic.getStringValue(0))
        }

        if (characteristic.descriptors.isNotEmpty()) {
            Text(text = "Descriptors", color = Color.LightGray)
        }

        characteristic.descriptors.forEach { descriptor ->
            if (descriptor.value != null) {
                Text(text = descriptor.uuid.toString())
                Text(text = descriptor.value.decodeToString())
            }
        }
    }
}

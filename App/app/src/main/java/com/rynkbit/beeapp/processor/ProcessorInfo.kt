package com.rynkbit.beeapp.processor

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rynkbit.beeapp.bluetooth.BluetoothConnection
import com.rynkbit.beeapp.bluetooth.GattCallback
import com.rynkbit.beeapp.ui.theme.defaultPadding
import com.rynkbit.beeapp.ui.theme.doublePadding
import com.rynkbit.beeapp.ui.theme.triplePadding

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@SuppressLint("MissingPermission")
@Composable
fun ProcessorInfo() {
    val viewModel: ProcessorInfoViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner) {
        val eventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> connect(viewModel)
                Lifecycle.Event.ON_PAUSE -> disconnect(viewModel)

            }
        }

        lifecycleOwner.lifecycle.addObserver(eventObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(eventObserver)
        }
    }

    val temperature by viewModel.temperature.observeAsState("No read")

    Column {
        Button(onClick = {
            disconnect(viewModel)
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(defaultPadding)) {
            Text(text = "Disconnect")
        }

        LazyVerticalGrid(cells = GridCells.Fixed(2)) {
            item {
                TemperatureCard(temperature)
            }
            item {
                PumpControlCard()
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun connect(viewModel: ProcessorInfoViewModel) {
    GattCallback().onServiceDiscovered.add(viewModel::onServiceDiscovered)
    GattCallback().onCharacteristicRead.add(viewModel::onCharacteristicRead)
    GattCallback().onCharacteristicChanged.add(viewModel::onCharacteristicChanged)
    BluetoothConnection().bluetoothGatt?.discoverServices()
}

@SuppressLint("MissingPermission")
private fun disconnect(viewModel: ProcessorInfoViewModel) {
    GattCallback().onServiceDiscovered.remove(viewModel::onServiceDiscovered)
    GattCallback().onCharacteristicRead.remove(viewModel::onCharacteristicRead)
    GattCallback().onCharacteristicChanged.remove(viewModel::onCharacteristicChanged)
    BluetoothConnection().bluetoothGatt?.disconnect()
}

@SuppressLint("MissingPermission")
@ExperimentalMaterialApi
@Preview
@Composable
fun PumpControlCard() {
    val viewModel: ProcessorInfoViewModel = viewModel()
    val enabled by viewModel.pumpEnabled.observeAsState(false)
    val state by viewModel.pumpState.observeAsState("")

    Card(modifier = Modifier
        .padding(defaultPadding)
        .fillMaxWidth(), elevation = doublePadding,
        enabled = enabled,
        onClick = { viewModel.togglePump() }) {
        Column(Modifier.padding(triplePadding)) {
            Text(
                text = "Pump control",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(doublePadding))
            Text(
                text = if (state == "OFF") "Start pump" else "Stop pump",
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
fun TemperatureCard(temperature: String = "20°C") {
    Card(
        modifier = Modifier
            .padding(defaultPadding)
            .fillMaxWidth(), elevation = doublePadding
    ) {
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

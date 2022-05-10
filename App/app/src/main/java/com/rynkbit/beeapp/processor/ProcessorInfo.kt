package com.rynkbit.beeapp.processor

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rynkbit.beeapp.ui.theme.defaultPadding
import com.rynkbit.beeapp.ui.theme.doublePadding
import com.rynkbit.beeapp.ui.theme.largePadding
import java.util.*

@Composable
fun ProcessorInfo() {
    val viewModel: ProcessorInfoViewModel = viewModel()
    val services = viewModel.bluetoothGatt.services

    LazyColumn {
        services.forEach { gattService ->
            item {
                GattService(gattService)
            }
        }
    }
}

@Preview
@Composable
fun GattService(gattService: BluetoothGattService = BluetoothGattService(
    UUID.randomUUID(), BluetoothGattService.SERVICE_TYPE_PRIMARY)) {
    Card {
        Column(modifier = Modifier.padding(doublePadding)) {
            Text(text = "UUID", color = Color.LightGray)
            Text(text = gattService.uuid.toString())
            Spacer(modifier = Modifier.height(defaultPadding))
            Text(text = "Characteristics", color = Color.LightGray)
            LazyColumn {
                gattService.characteristics.forEach { characteristic ->
                    item {
                        Divider()
                        GattCharacteristic(characteristic)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GattCharacteristic(characteristic: BluetoothGattCharacteristic = BluetoothGattCharacteristic(
    UUID.randomUUID(), BluetoothGattService.SERVICE_TYPE_PRIMARY, BluetoothGattCharacteristic.PERMISSION_READ
)) {
    Column {
        Text(text = "UUID", color = Color.LightGray)
        Text(text = characteristic.uuid.toString())
        Text(text = characteristic.value.toString())
        Text(text = "Content", color = Color.LightGray)
        Text(text = characteristic.value.contentToString())
    }
}

package com.rynkbit.beeapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rynkbit.beeapp.processor.ProcessorInfo
import com.rynkbit.beeapp.ui.theme.BeeAppTheme

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Main()
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
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
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "bluetooth/finder") {
                composable("bluetooth/finder"){
                    BluetoothFinder(navController)
                }
                composable("processor/info") {
                    ProcessorInfo()
                }
            }
        }
    }
}

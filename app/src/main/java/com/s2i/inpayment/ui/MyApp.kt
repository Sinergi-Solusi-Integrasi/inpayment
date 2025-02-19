package com.s2i.inpayment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.s2i.inpayment.ui.components.NetworkContent
import com.s2i.inpayment.ui.components.navigation.AppNavigation
import org.koin.compose.KoinContext

@Composable
fun MyApp() {
    KoinContext {
        val navController = rememberNavController()
        val context = LocalContext.current
        AppNavigation(navController = navController, context = context)
    }
}
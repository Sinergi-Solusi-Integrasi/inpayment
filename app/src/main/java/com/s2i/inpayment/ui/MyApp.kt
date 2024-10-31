package com.s2i.inpayment.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.s2i.inpayment.ui.components.navigation.AppNavigation
import org.koin.compose.KoinContext

@Composable
fun MyApp() {
    KoinContext {
        val navController = rememberNavController()
        AppNavigation(navController = navController)

    }
}
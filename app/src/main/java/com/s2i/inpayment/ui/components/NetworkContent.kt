package com.s2i.inpayment.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.s2i.common.utils.networkmanager.NetworkUtils
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.MyApp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkContent() {
    val isNetworkAvailable by NetworkUtils.isNetworkAvailable.collectAsState(initial = true)
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showErrorSheet by remember { mutableStateOf(false) }

    //launches a coroutine when the value of isNetworkAvailable changes memantau perubahan jaringan
    LaunchedEffect(isNetworkAvailable) {
        if (!isNetworkAvailable && !showErrorSheet) {
            showErrorSheet = true
            coroutineScope.launch {
                sheetState.show()
            }
        } else if (isNetworkAvailable && showErrorSheet) {
            coroutineScope.launch {
                sheetState.hide()
                showErrorSheet = false
            }
        }
    }

    // tampilkan reusablebottomseet hanya jika `showerrorsheetaktif` aktif
    if (showErrorSheet) {
        ReusableBottomSheet(
            imageRes = R.drawable.ic_networks_errors,
            message = "Tidak ada koneksi internet. Silakan periksa koneksi Anda.",
            sheetState = sheetState,
            onDismiss = {
                coroutineScope.launch { sheetState.hide() }
                showErrorSheet = false
            }
        )
    }
}



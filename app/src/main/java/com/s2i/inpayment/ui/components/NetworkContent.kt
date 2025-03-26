package com.s2i.inpayment.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.s2i.common.utils.networkmanager.NetworkUtils
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.MyApp
import com.s2i.inpayment.ui.components.button.SplitButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkContent(
    onRetry: () -> Unit = {}
) {
    val isNetworkAvailable by NetworkUtils.isNetworkAvailable.collectAsState(initial = true)
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showErrorSheet by remember { mutableStateOf(false) }

    //launches a coroutine when the value of isNetworkAvailable changes memantau perubahan jaringan
    LaunchedEffect(isNetworkAvailable) {
        if (!isNetworkAvailable && !showErrorSheet) {
            showErrorSheet = true
            coroutineScope.launch { sheetState.show() }
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
            },
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Buka Pengaturan
                    SplitButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        },
                        icon = Icons.Filled.Settings,
                        label = "Settings",
                        isSelected = false,
                    )
                    // Coba Lagi
                    SplitButton(
                        onClick = {
                            onRetry()
                        },
                        icon = Icons.Filled.Replay,
                        label = "Coba lagi",
                        isSelected = false,
                    )
                }
            }
        )
    }
}


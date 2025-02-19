package com.s2i.inpayment.ui.components.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.s2i.inpayment.R

@Composable
fun PreviewPhotoScreen(
    navController: NavController
){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top Bar with Close and Help buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    WindowInsets.statusBars.asPaddingValues() // Tambahkan padding sesuai status bar
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .size(40.dp) // Pastikan ukuran tombol cukup besar
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), // Warna lingkaran semi transparan
                            shape = CircleShape
                        )
                        .padding(8.dp) // Tambahkan padding di dalam tombol
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onPrimary, // Warna ikon
                        modifier = Modifier.size(24.dp) // Ukuran ikon
                    )
                }
                IconButton(onClick = { /* Help functionality */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_help), // Replace with your help icon
                        contentDescription = "Help",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPreviewPhotoScreen(){
    PreviewPhotoScreen(navController = NavController(context = LocalContext.current))
}
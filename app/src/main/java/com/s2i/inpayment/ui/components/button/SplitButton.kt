package com.s2i.inpayment.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapVerticalCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.theme.DarkTeal40
import com.s2i.inpayment.ui.theme.GreenSkyLight21
import com.s2i.inpayment.ui.theme.GreenTealLight20

@Composable
fun SplitReceiptBottomBar() {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .navigationBarsPadding()
            .background(Color(0xFF1C1C1E)), // Warna latar belakang dark
        containerColor = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
                SplitButton(
                    icon = Icons.Default.ChangeCircle,
                    label = "Switch Vehicles",
                    isSelected = false,
                    onClick = { /* Handle Switch Vehicles */ }
                )

                SplitButton(
                    icon = Icons.Default.Key,
                    label = "Lend Vehicles",
                    isSelected = false,
                    onClick = { /* TODO: Handle split by scanning */ }
                )

                SplitButton(
                    icon = Icons.Default.SwapVerticalCircle,
                    label = "Pull Loan",
                    isSelected = false,
                    onClick = { /* TODO: Handle share receipt */ }
                )
        }
    }
}


@Composable
fun SplitButton(icon: ImageVector? = null, painter: Painter? = null, label: String, isLoading: Boolean? = null, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) GreenSkyLight21 else DarkTeal40
    val textColor = if (isSelected) Color.Black else Color.Gray
    val iconColor = MaterialTheme.colorScheme.background

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(120.dp) // Lebar tombol dibuat lebih kecil agar sesuai dengan contoh
                .height(35.dp) // Tinggi tombol lebih proporsional
                .padding(horizontal = 4.dp)
        ) {
            if (isLoading == true) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when {
                        icon != null -> Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = iconColor,
                            modifier = Modifier
                                .size(20.dp)
                        )

                        painter != null -> Icon(
                            painter = painter,
                            contentDescription = label,
                            tint = iconColor,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
//                    maxLines = 1
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SplitReceiptBottomBarPreview() {
    MaterialTheme {
        SplitReceiptBottomBar()
    }
}
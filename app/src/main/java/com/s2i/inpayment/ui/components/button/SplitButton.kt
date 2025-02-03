package com.s2i.inpayment.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SplitReceiptBottomBar() {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C1E)), // Warna latar belakang dark
        containerColor = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SplitButton(
                icon = Icons.Default.ChangeCircle, // Gunakan ikon yang sesuai
                label = "Switch Vehicles",
                isSelected = false, // Highlight tombol pertama
                onClick = { /* TODO: Handle split by amount */ }
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
fun SplitButton(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFF263238) else Color(0xFF1C1C1E)
    val textColor = if (isSelected) Color.White else Color.Gray

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplitReceiptBottomBarPreview() {
    MaterialTheme {
        SplitReceiptBottomBar()
    }
}
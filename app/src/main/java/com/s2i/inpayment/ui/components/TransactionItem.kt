package com.s2i.inpayment.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun TransactionItem(title: String, description: String, amount: String, isNegative: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Text(
            text = amount,
            color = if (isNegative) Color.Red else Color.Green,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End
        )
    }
}

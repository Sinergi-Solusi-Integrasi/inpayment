package com.s2i.inpayment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s2i.inpayment.ui.theme.GreenTeal40

@Composable
fun TransactionItem(
    title: String,
    description: String,
    amount: String,
    isNegative: Boolean,
    dateTime: String,
    modifier: Modifier = Modifier // Tambahkan parameter modifier di sini
) {
    Column(
        modifier = modifier // Gunakan parameter modifier yang telah ditambahkan
            .fillMaxWidth()
            .clickable { /* Handle TransactionItem click */ } // Membuat seluruh item bisa diklik
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column for title and description
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title.ifEmpty { "Transaksi Tanpa Judul" },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text( // Menambahkan tampilan waktu transaksi
                    text = dateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Amount Text
            Text(
                text = amount,
                color = if (isNegative) Color.Red else GreenTeal40,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                textAlign = TextAlign.End,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        // Divider between items
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 2.dp),
            thickness = 0.1.dp,
            color = Color.LightGray
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTransactionItem() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        // Preview for negative cash flow transaction
        TransactionItem(
            title = "GT Fatmawati 1",
            description = "Pembayaran",
            amount = "-Rp 9.500",
            dateTime = "2222",
            isNegative = true
        )
        // Preview for another negative cash flow transaction
        TransactionItem(
            title = "INPayment",
            description = "Biaya Top Up",
            amount = "-Rp 1.000",
            dateTime = "2222",
            isNegative = true
        )
        // Preview for positive cash flow transaction
        TransactionItem(
            title = "Bank BCA",
            description = "Top Up",
            amount = "+Rp 100.000",
            dateTime = "2222",
            isNegative = false
        )
    }
}

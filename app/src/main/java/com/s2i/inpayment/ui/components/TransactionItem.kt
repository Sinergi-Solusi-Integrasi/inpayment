package com.s2i.inpayment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.GreenTeal40

@Composable
fun TransactionItem(
    title: String,
    trxType: String? = null,
    description: String,
    amount: String,
    isNegative: Boolean,
    dateTime: String,
    transactionId: String, // Tambahkan parameter transactionId
    iconResource: Int = R.drawable.`in`, // Parameter icon tambahan
    onClick: (String) -> Unit, // Callback untuk navigasi
    modifier: Modifier = Modifier // Tambahkan parameter modifier di sini
) {
    Column(
        modifier = modifier // Gunakan parameter modifier yang telah ditambahkan
            .fillMaxWidth()
            .clickable { onClick(transactionId) } // Membuat seluruh item bisa diklik
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon di sebelah kiri
            Box(
                modifier = Modifier
                    .size(40.dp)
            ){
                Icon(
                    painter = painterResource(id = iconResource),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified // Menggunakan warna default dari ikon
                )
            }
            // Column for title and description
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title.ifEmpty { "Transaksi Tanpa Judul" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp), // Menyesuaikan font
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                if(trxType != null){
                    Text(
                        text = trxType,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), // Menyesuaikan font
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), // Menyesuaikan font
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text( // Menambahkan tampilan waktu transaksi
                    text = dateTime,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), // Menyesuaikan font
                    color = Color.Gray
                )
            }

            // Amount Text
            Text(
                text = amount,
                color = if (isNegative) Color.Red else GreenTeal40,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
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
            trxType = "Top Up",
            description = "Pembayaran",
            amount = "-Rp 9.500",
            dateTime = "2222",
            isNegative = true,
            transactionId = "trx_001",
            onClick = { transactionId ->
                println("Clicked on transaction ID: $transactionId")
            }
        )
        // Preview for another negative cash flow transaction
        TransactionItem(
            title = "INPayment",
            trxType = "Top Up",
            description = "Biaya Top Up",
            amount = "-Rp 1.000",
            dateTime = "2222",
            isNegative = true,
            transactionId = "trx_001",
            onClick = { transactionId ->
                println("Clicked on transaction ID: $transactionId")
            }
        )
        // Preview for positive cash flow transaction
        TransactionItem(
            title = "Bank BCA",
            trxType = "Top Up",
            description = "Top Up",
            amount = "+Rp 100.000",
            dateTime = "2222",
            isNegative = false,
            transactionId = "trx_001",
            onClick = { transactionId ->
                println("Clicked on transaction ID: $transactionId")
            }
        )
    }
}

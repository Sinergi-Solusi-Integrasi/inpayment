package com.s2i.inpayment.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.common.utils.date.Dates
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.inpayment.ui.theme.BrightTeal20

@Composable
fun HistoryCard(
//    dateLabel: String,
    transaction: List<HistoryBalanceModel>,
    onTransactionClick: (String) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
//        Text(
//            text = dateLabel,
//            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
//            color = MaterialTheme.colorScheme.onTertiary,
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
//        )

        Spacer(modifier = Modifier.width(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                , // Limit height for the transaction history
            elevation = CardDefaults.elevatedCardElevation(0.dp),
            shape = MaterialTheme.shapes.medium
        ){
            Column(
                modifier = Modifier
                    .background(Color.White, shape = MaterialTheme.shapes.small.copy(all = CornerSize(10.dp)))
                    .padding(16.dp)
            ) {

                transaction.forEach{ transaction ->
                    val dateTimeFormatted = Dates.formatDate(
                        Dates.parseIso8601(
                            transaction.trxDate
                        )
                    )
                    Log.d("HistoryCard", "Processing transaction with cashFlow: ${transaction.cashFlow}")
                    TransactionItem(
                        title = transaction.title.ifEmpty { " " },
                        description = when (transaction.paymentMethod) {
                            "WALLET_CASH" -> "Bablas Saldo"
                            "BANK_TRANSFER" -> "Bank Transfer"
                            else -> transaction.paymentMethod
                        },
                        amount = if (transaction.cashFlow == "MONEY_OUT") "-${ RupiahFormatter.formatToRupiah(transaction.amount)}" else "+${RupiahFormatter.formatToRupiah(transaction.amount)}",
                        isNegative = transaction.cashFlow == "MONEY_OUT",
                        dateTime = dateTimeFormatted,
                        transactionId = transaction.transactionId,
                        onClick = {
                            onTransactionClick(transaction.transactionId) // Panggil lambda dengan transactionId
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }
            }
        }
    }

}

// Date Header
@Composable
fun DateHeader(dateLabel: String){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrightTeal20)
            .padding(vertical = 8.dp)
    ){
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}
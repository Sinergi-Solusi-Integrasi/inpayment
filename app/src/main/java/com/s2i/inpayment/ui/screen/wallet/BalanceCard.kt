package com.s2i.inpayment.ui.screen.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.domain.entity.model.balance.BalanceModel
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.theme.DarkTeal40

@Composable
fun BalanceCard(
    navController: NavController,
    balanceState: BalanceModel?,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // Adjust height as needed
            .padding(8.dp), // Padding around the card
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkTeal40) // Apply the gradient brush here
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Saldo",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBalanceVisible) balanceState?.let { RupiahFormatter.formatToRupiah(it.balance) } ?: "Loading..." else "****", // Update balance value
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            imageVector = if(isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isBalanceVisible) "Hide Balance" else "Show Balance",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Top-Up Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_topup), // Replace with top-up icon
                            contentDescription = "Top Up",
                            modifier = Modifier
                                .clickable {

                                },
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Top Up", color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        //riwayat
                        Icon(
                            Icons.Default.Receipt, // Replace with history icon
                            contentDescription = "Riwayat",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("history_screen") {
                                        launchSingleTop = true
                                    }
                                }
                                .size(16.dp)
                            ,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Riwayat", color = Color.White)
                    }
                }
            }
        }
    }
}
package com.s2i.inpayment.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.TransactionItem
import com.s2i.inpayment.ui.viewmodel.HomeViewModel

// In HomeScreen.kt

@Composable
fun HomeScreen(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val transactions = viewModel.transactionList
    val balance = viewModel.balance

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) { paddingValues ->

        // Section for the top part with the balance card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .background(Color(0xFF11526B)) // The color from your example
                .padding(16.dp)
        ) {
            // Header with Logo, Notification, and Profile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Menjaga jarak antara logo dan profil
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo on the left
                Image(
                    painter = painterResource(id = R.drawable.second_logo), // Replace with the correct logo resource
                    contentDescription = "Logo",
                    modifier = Modifier.size(135.dp, 27.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Notification icon in the center
                Row(
                    verticalAlignment = Alignment.CenterVertically // Sejajarkan notifikasi dan profil secara vertikal
                ) {
                    IconButton(onClick = { /* Handle notification click */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Profile picture on the right
                    Image(
                        painter = painterResource(id = R.drawable.logo), // Replace with the correct profile image
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.Gray, shape = MaterialTheme.shapes.medium)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            // Balance Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Adjusting height to match the design
                    .padding(8.dp), // Padding around the card
                colors = CardDefaults.cardColors(containerColor = Color(0xFF008080)) // Custom card color
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                            text = "Rp 150.000", // Update the balance value
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { /* Handle visibility toggle */ }) {
                            Icon(Icons.Default.Visibility, contentDescription = "Toggle Visibility", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Top Up", color = Color.White)
                        }

                        // History Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_riwayat), // Replace with history icon
                                contentDescription = "Riwayat",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Riwayat", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Section for Pemasukan and Pengeluaran
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Card for Pemasukan
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp), // Space between the two cards
                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Pemasukan", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Rp 100.000", style = MaterialTheme.typography.bodyLarge, color = Color.Green)
                        Text(text = "Top-Up M-BCA", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Card for Pengeluaran
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp), // Space between the two cards
                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Pengeluaran", style = MaterialTheme.typography.titleMedium)
                        Text(text = "-Rp 9.500", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
                        Text(text = "GT-Fatmawati 1", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Section for transaction history with rounded corners at the top
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 420.dp) // Start this part below the top section
                .background(Color.White, shape = MaterialTheme.shapes.small.copy(all = CornerSize(10.dp))) // Rounded corner for the bottom section
                .padding(16.dp)
        ) {
            // Transaction History Section
            Text(text = "Riwayat Transaksi", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp), // Limit height for the transaction history
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                shape = MaterialTheme.shapes.medium // Corner radius for the card
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(32.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transactions.take(3)) { transaction -> // Show the latest 3 transactions
                            TransactionItem(
                                title = transaction.title,
                                description = transaction.description,
                                amount = transaction.amount,
                                isNegative = transaction.isNegative
                            )
                        }
                    }
                    // "See More" button
                    TextButton(onClick = { /* Handle see more */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Lihat Riwayat",
                            style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}




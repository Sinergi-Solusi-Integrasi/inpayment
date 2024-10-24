package com.s2i.inpayment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.s2i.core.model.transaction.Transaction

class HomeViewModel : ViewModel() {
    val balance = mutableStateOf(150_000)

    val transactionList = mutableStateListOf(
        Transaction("GT Fatmawati 1", "Pembayaran", "-Rp 9.500", true),
        Transaction("INPayment", "Biaya Top Up", "-Rp 1.000", true),
        Transaction("Bank BCA", "Top Up", "+Rp 100.000", false)
    )
}

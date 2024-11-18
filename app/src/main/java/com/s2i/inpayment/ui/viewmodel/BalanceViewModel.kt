package com.s2i.inpayment.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.common.utils.date.Dates
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.balance.BalanceModel
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.balance.InOutBalanceModel
import com.s2i.domain.entity.model.balance.IncomeExpenseModel
import com.s2i.domain.entity.model.balance.IncomeExpensesTrxModel
import com.s2i.domain.usecase.auth.LoginUseCase
import com.s2i.domain.usecase.auth.RegisterUseCase
import com.s2i.domain.usecase.balance.GetBalanceUseCase
import com.s2i.domain.usecase.balance.GetHistoryBalanceUseCase
import com.s2i.domain.usecase.balance.GetInOutBalanceUseCase
import com.s2i.domain.usecase.balance.GetIncomeExpensesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class BalanceViewModel(
    private val balanceUseCase: GetBalanceUseCase,
    private val inOutBalanceUseCase: GetInOutBalanceUseCase,
    private val historyUseCase: GetHistoryBalanceUseCase,
    private val incomeExpenseUseCase: GetIncomeExpensesUseCase,
    private val sessionManager: SessionManager
): ViewModel() {

    private val _balance = MutableStateFlow<BalanceModel?>(null)
    val balance: StateFlow<BalanceModel?> = _balance

    private val _triLastTransaction = MutableStateFlow<List<HistoryBalanceModel>>(emptyList())
    val triLastTransaction: StateFlow<List<HistoryBalanceModel>> = _triLastTransaction

    private val _historyTransaction = MutableStateFlow<Map<String, List<HistoryBalanceModel>>>(emptyMap())
    val historyTransaction: StateFlow<Map<String, List<HistoryBalanceModel>>> = _historyTransaction

    private val _incomeExpenses = MutableStateFlow<IncomeExpenseModel?>(null)
    val incomeExpenses: StateFlow<IncomeExpenseModel?> = _incomeExpenses

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Fungsi untuk mengelompokkan transaksi berdasarkan logika tanggal
    private fun groupTransactionsByDate(transactions: List<HistoryBalanceModel>): Map<String, List<HistoryBalanceModel>> {
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        // Using Calender to fixing issues midnight reset
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        calendar.set(Calendar.HOUR_OF_DAY,0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)
        val normalizedNow = calendar.timeInMillis

        return transactions.groupBy { transaction ->
            val transactionDate = Dates.parseIso8601(transaction.trxDate)
            val dateString = dateFormat.format(Date(transactionDate))

            calendar.timeInMillis = transactionDate
            calendar.set(Calendar.HOUR_OF_DAY,0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND,0)
            calendar.set(Calendar.MILLISECOND,0)
            val normalizedtransactionDate = calendar.timeInMillis

            val differenceDays = TimeUnit.MILLISECONDS.toDays(normalizedNow - normalizedtransactionDate)
            Log.d("BalanceViewModel", "Parsed transaction date: $transactionDate for trxDate: ${transaction.trxDate}")

            when {
                differenceDays == 0L -> "Hari ini"
                differenceDays == 1L -> "Kemarin"
                else -> dateString
            }
        } .also { groupedMap ->
            Log.d("BalanceViewModel", "Grouped Transactions: $groupedMap")
        }
    }

    fun fetchBalance() {
        viewModelScope.launch {
            _loading.value = true
//            val token = sessionManager.accessToken ?: return@launch
            try {
                _balance.value = balanceUseCase()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchTriLastTransaction() {
        viewModelScope.launch {
            _loading.value = true

            try{
                val inOutBalance = inOutBalanceUseCase()

                // Parsing and sorting transactions by trxDate in descending order
                val sortedHistory = inOutBalance.data.sortedByDescending {
                    Dates.parseIso8601(it.trxDate)
                }

                // Update the state with the 3 most recent transactions
                _triLastTransaction.value = sortedHistory.take(3)

                // Log the sorted transactions
                Log.d("BalanceViewModel", "Tiga transaksi terbaru yang diambil:")
                _triLastTransaction.value.forEach {
                    Log.d("BalanceViewModel", "Transaction ID: ${it.transactionId}, Title: ${it.title}, Date: ${it.trxDate}, Amount: ${it.amount}")
                }
            } catch (e: Exception){
                _error.value = e.message
                Log.e("BalanceViewModel", "Error saat mengambil transaksi: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    //incomeexpenses
    fun fetchInComeExpenses() {
        viewModelScope.launch {
            _loading.value = true
            try{
                val result = incomeExpenseUseCase()
                Log.d("BalanceViewModel", "Fetched Income Transaction: ${result.data?.incomeTrx?.amount}")
                _incomeExpenses.value = result
            } catch (e: Exception){
                _error.value = e.message
                Log.e("BalanceViewModel", "Error fetching income and expenses: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    // History Balance
    fun fetchHistory(){
        viewModelScope.launch {
            _loading.value = true
            try{
                val result = historyUseCase()
                Log.d("BalanceViewModel", "Fetched Transactions: ${result.data}")
                _historyTransaction.value = groupTransactionsByDate(result.data)
            } catch (e: Exception){
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
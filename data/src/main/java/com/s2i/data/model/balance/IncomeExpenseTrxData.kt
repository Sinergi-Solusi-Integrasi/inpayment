package com.s2i.data.model.balance

import com.google.gson.annotations.SerializedName

data class IncomeExpenseTrxData(
    @SerializedName("last_income_transaction")
    val incomeTrx: IncomeTrx?,
    @SerializedName("last_expense_transaction")
    val expenseTrx: ExpenseTrx?
)

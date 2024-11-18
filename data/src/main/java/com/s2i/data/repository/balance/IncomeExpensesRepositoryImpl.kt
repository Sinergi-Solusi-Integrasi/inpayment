
package com.s2i.data.repository.balance

import com.s2i.data.remote.client.ApiServices
import com.s2i.domain.entity.model.balance.ExpenseTrxModel
import com.s2i.domain.entity.model.balance.IncomeExpenseModel
import com.s2i.domain.entity.model.balance.IncomeExpensesTrxModel
import com.s2i.domain.entity.model.balance.IncomeTrxModel
import com.s2i.domain.repository.balance.IncomeExpenseRepository

// Updated Repository Implementation
class IncomeExpensesRepositoryImpl(
    private val apiServices: ApiServices
) : IncomeExpenseRepository {
    override suspend fun getIncomeExpenses(): IncomeExpenseModel {
        val response = apiServices.incomeExpense()

        val responseData = response.data

        return IncomeExpenseModel(
            code = response.code,
            message = response.message,
            data = IncomeExpensesTrxModel(
                incomeTrx = responseData?.incomeTrx?.let {
                    IncomeTrxModel(
                        trxId = it.trxId,
                        userId = it.userId,
                        accountNumber = it.accountNumber,
                        refId = it.refId,
                        amount = it.amount,
                        feeAmount = it.feeAmount,
                        cashflow = it.cashflow,
                        trxType = it.trxType,
                        paymentMethods = it.paymentMethods,
                        beginningBalance = it.beginningBalance,
                        endingBalance = it.endingBalance,
                        title = it.title,
                        trxStatus = it.trxStatus,
                        trxDates = it.trxDates,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                } ?: IncomeTrxModel(
                    trxId = "",
                    userId = "",
                    accountNumber = "",
                    refId = "",
                    amount = 0,
                    feeAmount = 0,
                    cashflow = "",
                    trxType = "",
                    paymentMethods = "",
                    beginningBalance = 0,
                    endingBalance = 0,
                    title = "No Income",
                    trxStatus = "",
                    trxDates = "",
                    createdAt = "",
                    updatedAt = ""
                ),
                expenseTrx = responseData?.expenseTrx?.let {
                    ExpenseTrxModel(
                        trxId = it.trxId,
                        userId = it.userId,
                        accountNumber = it.accountNumber,
                        refId = it.refId,
                        amount = it.amount,
                        feeAmount = it.feeAmount,
                        cashflow = it.cashflow,
                        trxType = it.trxType,
                        paymentMethods = it.paymentMethods,
                        beginningBalance = it.beginningBalance,
                        endingBalance = it.endingBalance,
                        title = it.title,
                        trxStatus = it.trxStatus,
                        trxDates = it.trxDates,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                } ?: ExpenseTrxModel(
                    trxId = "",
                    userId = "",
                    accountNumber = "",
                    refId = "",
                    amount = 0,
                    feeAmount = 0,
                    cashflow = "",
                    trxType = "",
                    paymentMethods = "",
                    beginningBalance = 0,
                    endingBalance = 0,
                    title = "No Expense",
                    trxStatus = "",
                    trxDates = "",
                    createdAt = "",
                    updatedAt = ""
                )
            )
        )
    }
}



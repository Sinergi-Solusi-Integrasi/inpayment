package com.s2i.domain.repository.balance

import com.s2i.domain.entity.model.balance.IncomeExpenseModel

interface IncomeExpenseRepository {
    suspend fun getIncomeExpenses(): IncomeExpenseModel
}
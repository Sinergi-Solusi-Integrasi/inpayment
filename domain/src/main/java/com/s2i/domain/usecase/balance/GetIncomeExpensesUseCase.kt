package com.s2i.domain.usecase.balance

import com.s2i.domain.entity.model.balance.IncomeExpenseModel
import com.s2i.domain.repository.balance.IncomeExpenseRepository

class GetIncomeExpensesUseCase(
    private val inComeExpenseRepo: IncomeExpenseRepository
) {
    suspend operator fun invoke(): IncomeExpenseModel{
        return inComeExpenseRepo.getIncomeExpenses()

    }
}
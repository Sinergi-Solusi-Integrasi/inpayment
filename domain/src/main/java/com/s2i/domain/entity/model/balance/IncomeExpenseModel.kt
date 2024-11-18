package com.s2i.domain.entity.model.balance

import com.google.gson.annotations.SerializedName

data class IncomeExpenseModel(
    val code: Int,
    val message: String,
    val data: IncomeExpensesTrxModel?
)

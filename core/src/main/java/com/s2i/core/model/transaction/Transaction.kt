package com.s2i.core.model.transaction

data class Transaction(
    val title: String,
    val description: String,
    val amount: String,
    val isNegative: Boolean
)
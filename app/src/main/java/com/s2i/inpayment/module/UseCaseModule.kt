package com.s2i.inpayment.module

import com.s2i.domain.usecase.auth.LoginUseCase
import com.s2i.domain.usecase.auth.RegisterUseCase
import com.s2i.domain.usecase.auth.TokenUseCase
import com.s2i.domain.usecase.balance.GetBalanceUseCase
import com.s2i.domain.usecase.balance.GetInOutBalanceUseCase
import com.s2i.domain.usecase.balance.GetHistoryBalanceUseCase
import com.s2i.domain.usecase.balance.GetIncomeExpensesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    // Use cases
    factoryOf(::LoginUseCase) // Inject AuthRepository into LoginUseCase
    factoryOf(::RegisterUseCase) // Inject AuthRepository into RegisterUseCase
    factoryOf(::GetBalanceUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::GetInOutBalanceUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::GetHistoryBalanceUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::GetIncomeExpensesUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::TokenUseCase)// Inject AuthRepository into GetBalanceUseCase


}
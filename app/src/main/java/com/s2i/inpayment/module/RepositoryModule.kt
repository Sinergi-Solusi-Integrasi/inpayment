package com.s2i.inpayment.module

import com.s2i.data.repository.auth.AuthRepositoryImpl
//import com.s2i.data.repository.auth.TokenRepositoryImpl
import com.s2i.data.repository.balance.BalanceRepositoryImpl
import com.s2i.data.repository.balance.InOutBalanceRepositoryImpl
import com.s2i.data.repository.balance.HistoryBalanceRepositoryImpl
import com.s2i.data.repository.balance.DetailTrxRepositoryImpl
import com.s2i.data.repository.balance.IncomeExpensesRepositoryImpl
import com.s2i.data.repository.users.UsersProfileRepositoryImpl
import com.s2i.data.repository.vehicles.VehiclesRepositoryImpl
import com.s2i.domain.repository.auth.AuthRepository
import com.s2i.domain.repository.auth.TokenRepository
import com.s2i.domain.repository.users.UsersProfileRepository
import com.s2i.domain.repository.vehicles.VehiclesRepository
import com.s2i.domain.repository.balance.BalanceRepository
import com.s2i.domain.repository.balance.DetailTrxRepository
import com.s2i.domain.repository.balance.HistoryBalanceRepository
import com.s2i.domain.repository.balance.InOutBalanceRepository
import com.s2i.domain.repository.balance.IncomeExpenseRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repoModule = module {
    // Bind AuthRepositoryImpl as AuthRepository
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::BalanceRepositoryImpl) { bind<BalanceRepository>() }
    singleOf(::InOutBalanceRepositoryImpl) { bind<InOutBalanceRepository>() }
    singleOf(::DetailTrxRepositoryImpl) { bind<DetailTrxRepository>() }
    singleOf(::HistoryBalanceRepositoryImpl) { bind<HistoryBalanceRepository>() }
    singleOf(::IncomeExpensesRepositoryImpl) { bind<IncomeExpenseRepository>() }
    singleOf(::UsersProfileRepositoryImpl) { bind<UsersProfileRepository>() }
    singleOf(::VehiclesRepositoryImpl) { bind<VehiclesRepository>() }
//    singleOf(::TokenRepositoryImpl) { bind<TokenRepository>()}
}
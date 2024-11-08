package com.s2i.inpayment.module

import com.s2i.data.repository.auth.AuthRepositoryImpl
import com.s2i.data.repository.auth.TokenRepositoryImpl
import com.s2i.data.repository.balance.BalanceRepositoryImpl
import com.s2i.domain.repository.auth.AuthRepository
import com.s2i.domain.repository.auth.TokenRepository
import com.s2i.domain.repository.balance.BalanceRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repoModule = module {
    // Bind AuthRepositoryImpl as AuthRepository
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::BalanceRepositoryImpl) { bind<BalanceRepository>() }
    singleOf(::TokenRepositoryImpl) { bind<TokenRepository>()}
}
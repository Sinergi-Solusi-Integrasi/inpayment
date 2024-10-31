package com.s2i.inpayment.module

import com.s2i.data.repository.auth.AuthRepositoryImpl
import com.s2i.domain.repository.auth.AuthRepository
import org.koin.dsl.module

val repoModule = module {
    // Bind AuthRepositoryImpl as AuthRepository
    single<AuthRepository> { AuthRepositoryImpl(get(),get()) }
}
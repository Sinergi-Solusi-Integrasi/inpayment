package com.s2i.inpayment.module

import com.s2i.domain.usecase.auth.LoginUseCase
import com.s2i.domain.usecase.auth.RegisterUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Use cases
    factory { LoginUseCase(get()) } // Inject AuthRepository into LoginUseCase
    factory { RegisterUseCase(get()) } // Inject AuthRepository into RegisterUseCase
}
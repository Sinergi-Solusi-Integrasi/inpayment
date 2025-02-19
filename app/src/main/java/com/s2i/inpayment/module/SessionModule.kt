package com.s2i.inpayment.module

import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.usecase.auth.TokenUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val sessionModule = module {
//    single { SessionManager(androidContext()) } // Inject context
}
package com.s2i.inpayment.module

import com.s2i.data.local.auth.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sessionModule = module {
    single { SessionManager(androidContext()) } // Inject context
}
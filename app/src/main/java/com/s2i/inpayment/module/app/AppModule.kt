package com.s2i.inpayment.module.app

import com.s2i.inpayment.module.repoModule
import com.s2i.inpayment.module.sessionModule
import com.s2i.inpayment.module.useCaseModule
import com.s2i.inpayment.module.viewModule
import org.koin.dsl.module

val appModule = module {
    includes(
        viewModule,
        repoModule,
        useCaseModule,
    )
}
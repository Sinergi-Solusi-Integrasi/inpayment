package com.s2i.inpayment.ui.viewmodel

import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.usecase.auth.LoginUseCase
import com.s2i.domain.usecase.auth.RegisterUseCase
import com.s2i.domain.usecase.balance.BalanceUseCase

class BalanceViewModel(
    private val balanceUseCase: BalanceUseCase,
    private val sessionManager: SessionManager
) {
}
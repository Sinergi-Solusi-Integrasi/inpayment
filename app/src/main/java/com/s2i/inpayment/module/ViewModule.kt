package com.s2i.inpayment.module


import com.s2i.inpayment.ui.viewmodel.HomeViewModel
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
//import com.s2i.inpayment.ui.viewmodel.TokenViewModel
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.UsersViewModel
import org.koin.core.module.dsl.viewModelOf

import org.koin.dsl.module

val viewModule = module {
    // ViewModels
    viewModelOf(::HomeViewModel) // Example HomeViewModel
    viewModelOf(::AuthViewModel) // Example HomeViewModel
    viewModelOf(::BalanceViewModel) // Example HomeViewModel
    viewModelOf(::UsersViewModel) // Example HomeViewModel
//    viewModelOf(::TokenViewModel) // Example HomeViewModel
}
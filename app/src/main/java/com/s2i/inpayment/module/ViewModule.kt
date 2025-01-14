package com.s2i.inpayment.module


import com.s2i.inpayment.ui.viewmodel.HomeViewModel
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import com.s2i.inpayment.ui.viewmodel.TokenViewModel
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import com.s2i.inpayment.ui.viewmodel.NotificationsViewModel
import com.s2i.inpayment.ui.viewmodel.ServicesViewModel
import com.s2i.inpayment.ui.viewmodel.UsersViewModel
import com.s2i.inpayment.ui.viewmodel.QrisViewModel
import org.koin.core.module.dsl.viewModelOf

import org.koin.dsl.module

val viewModule = module {
    // ViewModels
    viewModelOf(::HomeViewModel) // Example HomeViewModel
    viewModelOf(::AuthViewModel) // AuthViewModel
    viewModelOf(::BalanceViewModel) // BalanceViewModel
    viewModelOf(::UsersViewModel) // UsersViewModel
    viewModelOf(::VehiclesViewModel) // VehiclesViewModel
    viewModelOf(::NotificationsViewModel) // NotificationsViewModel
    viewModelOf(::ServicesViewModel) // ServicesViewModel)
    viewModelOf(::TokenViewModel) // Example HomeViewModel
    viewModelOf(::QrisViewModel)
}
package com.s2i.inpayment.module

import com.s2i.domain.usecase.auth.LoginUseCase
import com.s2i.domain.usecase.auth.RegisterUseCase
import com.s2i.domain.usecase.auth.TokenUseCase
import com.s2i.domain.usecase.auth.LogoutUseCase
import com.s2i.domain.usecase.notifications.services.ServicesUseCase
import com.s2i.domain.usecase.balance.GetBalanceUseCase
import com.s2i.domain.usecase.notifications.GetNotificationTrxUseCase
import com.s2i.domain.usecase.notifications.services.DevicesTokenUseCase
import com.s2i.domain.usecase.users.GetUsersUseCase
import com.s2i.domain.usecase.balance.GetInOutBalanceUseCase
import com.s2i.domain.usecase.balance.GetDetailTrxUseCase
import com.s2i.domain.usecase.balance.GetHistoryBalanceUseCase
import com.s2i.domain.usecase.balance.GetIncomeExpensesUseCase
import com.s2i.domain.usecase.vehicles.GetVehiclesUseCase
import com.s2i.domain.usecase.vehicles.EnableStatusUseCase
import com.s2i.domain.usecase.vehicles.RegistVehiclesUseCase
import com.s2i.domain.usecase.vehicles.GetDisableStatusUseCase
import com.s2i.domain.usecase.wallet.CreateQrisUseCase
import com.s2i.domain.usecase.wallet.OrderQueryQrisUseCase
import com.s2i.domain.usecase.wallet.TopupQrisUseCase
import com.s2i.domain.usecase.vehicles.ChangeVehiclesUseCase
import com.s2i.domain.usecase.vehicles.LendVehiclesUseCase
import com.s2i.domain.usecase.vehicles.LoansVehiclesUseCase
import com.s2i.domain.usecase.vehicles.PullLoansVehiclesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    // Use cases
    factoryOf(::LoginUseCase) // Inject AuthRepository into LoginUseCase
    factoryOf(::RegisterUseCase) // Inject AuthRepository into RegisterUseCase
    factoryOf(::GetBalanceUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::GetInOutBalanceUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::GetDetailTrxUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::GetHistoryBalanceUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::GetIncomeExpensesUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::GetUsersUseCase)// Inject UsersRepository into GetUsersUseCase
    factoryOf(::GetVehiclesUseCase)// Inject VehiclesRepository into GetVehiclesUseCase
    factoryOf(::EnableStatusUseCase)// Inject VehiclesRepository into GetVehiclesUseCase
    factoryOf(::RegistVehiclesUseCase)
    factoryOf(::GetDisableStatusUseCase)// Inject VehiclesRepository into GetVehiclesUseCase
    factoryOf(::GetNotificationTrxUseCase)// Inject VehiclesRepository into GetVehiclesUseCase
    factoryOf(::DevicesTokenUseCase)// Inject VehiclesRepository into GetVehiclesUseCase
    factoryOf(::LogoutUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::ServicesUseCase)
    factoryOf(::TokenUseCase)// Inject AuthRepository into GetBalanceUseCase
    factoryOf(::CreateQrisUseCase)
    factoryOf(::OrderQueryQrisUseCase)
    factoryOf(::TopupQrisUseCase)
    factoryOf(::ChangeVehiclesUseCase)
    factoryOf(::LendVehiclesUseCase)
    factoryOf(::LoansVehiclesUseCase)
    factoryOf(::PullLoansVehiclesUseCase)



}
package com.s2i.domain.usecase.notifications

import com.s2i.domain.entity.model.notification.NotitificationsTrxModel
import com.s2i.domain.entity.model.users.UsersProfileModel
import com.s2i.domain.repository.notifications.NotificationsRepository

class GetNotificationTrxUseCase(
    private val notififyTrxRepository: NotificationsRepository
) {
    suspend operator fun invoke() : NotitificationsTrxModel {
        return notififyTrxRepository.getNotifyTrx()
    }
}
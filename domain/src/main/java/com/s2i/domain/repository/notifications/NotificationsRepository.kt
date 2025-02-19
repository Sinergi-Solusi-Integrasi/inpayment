package com.s2i.domain.repository.notifications

import com.s2i.domain.entity.model.notification.NotitificationsTrxModel

interface NotificationsRepository {
    suspend fun getNotifyTrx(): NotitificationsTrxModel
}
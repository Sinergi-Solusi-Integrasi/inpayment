package com.s2i.domain.repository.users

import com.s2i.domain.entity.model.balance.InOutBalanceModel
import com.s2i.domain.entity.model.users.ProfileModel
import com.s2i.domain.entity.model.users.UsersProfileModel

interface UsersProfileRepository {
    suspend fun getUsersProfile() : UsersProfileModel
}
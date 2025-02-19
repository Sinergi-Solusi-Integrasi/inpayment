package com.s2i.domain.usecase.users

import com.s2i.domain.entity.model.users.ProfileModel
import com.s2i.domain.entity.model.users.UsersProfileModel
import com.s2i.domain.repository.users.UsersProfileRepository

class GetUsersUseCase(
    private val usersRepository: UsersProfileRepository
) {
    suspend operator fun invoke() : UsersProfileModel {
        return usersRepository.getUsersProfile()
    }

}
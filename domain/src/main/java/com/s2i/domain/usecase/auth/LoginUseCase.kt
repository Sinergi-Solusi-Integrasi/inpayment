    package com.s2i.domain.usecase.auth

    import com.s2i.domain.entity.model.auth.AuthModel
    import com.s2i.domain.repository.auth.AuthRepository

    class LoginUseCase(private val authRepository: AuthRepository){
        suspend operator fun invoke(username: String, password: String): Result<AuthModel> {
            return authRepository.login(username, password)
        }
    }
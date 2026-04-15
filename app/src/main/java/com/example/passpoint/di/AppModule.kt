package com.example.passpoint.di

import com.example.passpoint.data.remote.UserApi
import com.example.passpoint.data.repository.UserRepositoryImpl
import com.example.passpoint.domain.repository.Repository
import com.example.passpoint.domain.useCase.SignInUseCase
import com.example.passpoint.domain.useCase.SignUpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi): Repository
    {
        return UserRepositoryImpl(userApi)
    }
    @Provides
    @Singleton
    fun signUpUseCase(userRepository: Repository): SignUpUseCase {
        return SignUpUseCase(userRepository)
    }
    @Provides
    @Singleton
    fun signInUseCase(userRepository: Repository): SignInUseCase {
        return SignInUseCase(userRepository)
    }
}
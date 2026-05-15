package com.example.passpoint.di

import android.content.Context
import com.example.passpoint.data.remote.UserApi
import com.example.passpoint.data.repository.UserRepositoryImpl
import com.example.passpoint.domain.repository.Repository
import com.example.passpoint.domain.useCase.SignInUseCase
import com.example.passpoint.domain.useCase.SignUpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideUserRepository(
        userApi: UserApi,
        @ApplicationContext context: Context
    ): Repository {
        return UserRepositoryImpl(userApi, context)
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
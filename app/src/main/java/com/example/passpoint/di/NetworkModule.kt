package com.example.passpoint.di

import com.example.passpoint.data.remote.UserApi
import com.example.passpoint.data.remote.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
        return UserApiService.create()
    }
}
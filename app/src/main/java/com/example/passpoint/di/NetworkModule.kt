package com.example.passpoint.di

import android.content.Context
import com.example.passpoint.data.remote.UserApi
import com.example.passpoint.data.remote.UserApiService
import com.example.passpoint.domain.utils.AndroidNetworkMonitor
import com.example.passpoint.domain.utils.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return AndroidNetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideAndroidNetworkMonitor(@ApplicationContext context: Context): AndroidNetworkMonitor {
        return AndroidNetworkMonitor(context)
    }
}
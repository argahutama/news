package com.argahutama.news.di

import com.argahutama.news.BuildConfig
import com.argahutama.news.common.di.Flavor
import com.argahutama.news.remote.di.ApiKey
import com.argahutama.news.remote.di.BaseUrl
import com.argahutama.news.remote.di.IsDebug
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
    @Provides
    @Flavor
    fun provideFlavor() = BuildConfig.FLAVOR

    @Provides
    @BaseUrl
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @ApiKey
    fun provideApiKey() = BuildConfig.API_KEY

    @Provides
    @IsDebug
    fun provideIsDebug() = BuildConfig.DEBUG
}

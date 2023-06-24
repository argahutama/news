package com.argahutama.news.remote.di

import com.argahutama.news.remote.api.NewsApi
import com.argahutama.news.remote.interceptor.AcceptLanguageInterceptor
import com.google.gson.Gson
import com.mocklets.pluto.PlutoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IsDebug

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultAuth

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides
    fun provideAcceptLanguageInterceptor() = AcceptLanguageInterceptor()

    @Provides
    fun providePlutoInterceptor() = PlutoInterceptor()

    @Provides
    fun provideOkHttpClient(
        acceptLanguageInterceptor: AcceptLanguageInterceptor,
        plutoInterceptor: PlutoInterceptor,
        @IsDebug isDebug: Boolean,
    ): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
            .addInterceptor(acceptLanguageInterceptor)
            .connectTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
        if (isDebug) builder.addInterceptor(plutoInterceptor)
        return builder
    }

    @Provides
    @DefaultAuth
    @Singleton
    fun provideDefaultRetrofit(
        gson: Gson,
        builder: OkHttpClient.Builder,
        @BaseUrl baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .client(builder.build())
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun providePhotoApi(
        @DefaultAuth retrofit: Retrofit
    ): NewsApi = retrofit.create(NewsApi::class.java)
}
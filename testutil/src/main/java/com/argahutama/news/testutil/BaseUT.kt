package com.argahutama.news.testutil

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.components.SingletonComponent
import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

abstract class BaseUT {
    @Inject
    lateinit var mockWebServer: MockWebServer

    private var mShouldStart = false

    abstract val hiltRule: HiltAndroidRule

    @ExperimentalCoroutinesApi
    @Before
    open fun setUp() {
        hiltRule.inject()
        MockKAnnotations.init(this)
        Dispatchers.setMain(TestCoroutineDispatcher())
        startMockServer(true)
    }

    fun mockNetworkResponseWithFileContent(clazz: Class<*>, fileName: String, responseCode: Int) =
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(responseCode)
                .setBody(getJson(clazz, fileName))
        )

    fun getJson(clazz: Class<*>, path: String): String {
        val uri = clazz.classLoader!!.getResourceAsStream(path)
        return String(uri.readBytes())
    }

    fun <T> toJsonObject(response: String, clazz: Class<T>): T =
        Gson().fromJson(response, clazz)


    private fun startMockServer(shouldStart: Boolean) {
        if (shouldStart) {
            mShouldStart = shouldStart
        }
    }

    private fun stopMockServer() {
        if (mShouldStart) {
            mockWebServer.shutdown()
        }
    }

    @ExperimentalCoroutinesApi
    @After
    open fun tearDown() {
        Dispatchers.resetMain()
        stopMockServer()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object BaseUTModule {
        @Provides
        @Singleton
        fun provideMockWebServer() = MockWebServer()

        @Provides
        fun provideOkHttpClient(
        ): OkHttpClient.Builder = OkHttpClient.Builder()

        @Provides
        fun provideRetrofit(
            builder: OkHttpClient.Builder,
            mockWebServer: MockWebServer
        ) = Retrofit.Builder()
            .client(builder.build())
            .baseUrl(mockWebServer.url("/").toString())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }
}
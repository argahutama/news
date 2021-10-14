package com.argahutama.repository

import com.argahutama.news.model.response.NewsResponse
import com.argahutama.news.remote.api.NewsApi
import com.argahutama.news.remote.di.ApiKey
import com.argahutama.repository.extension.tryToReturn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    @ApiKey val apiKey: String,
    private val newsApi: NewsApi
) {
    suspend fun getNews(page: Int = 1): NewsResponse? {
        val response = newsApi.getNews(apiKey, page)
        return response.tryToReturn { response.body() }
    }
}
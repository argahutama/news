package com.argahutama.news.remote.api

import com.argahutama.news.model.response.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines?country=id&pageSize=10")
    suspend fun getNews(
        @Query("apiKey") apiKey: String,
        @Query("page") page: Int
    ): Response<NewsResponse>
}
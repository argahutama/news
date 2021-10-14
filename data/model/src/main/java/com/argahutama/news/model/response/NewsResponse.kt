package com.argahutama.news.model.response

import com.argahutama.news.model.Article
import com.google.gson.annotations.SerializedName

data class NewsResponse (
    @SerializedName("status") val status: String?,
    @SerializedName("totalResults") val totalResults: Long?,
    @SerializedName("articles") val articles: List<Article>?
)
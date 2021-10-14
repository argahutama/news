package com.argahutama.news.common.navigation

import com.argahutama.news.model.Article

object Extra {
    const val ID = "extra:id"
    const val PHOTO = "extra:photo"
}

sealed class NavigationDirection(val extras: Map<String, Any?>) {
    object Home : NavigationDirection(mapOf())
    object ConnectionError : NavigationDirection(mapOf())
    object ServerError : NavigationDirection(mapOf())
    class Detail(article: Article) : NavigationDirection(mapOf(Extra.PHOTO to article))
}
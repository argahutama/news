package com.argahutama.news.home

import com.argahutama.news.common.base.BaseViewModel
import com.argahutama.news.model.Article
import com.argahutama.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : BaseViewModel<HomeViewState>() {
    private var page: Int = 1
    private var articleLoaded = 0
    var articles = mutableListOf<Article>()
        private set
    var shouldLoadMore = false
        private set

    fun fetchInitialData() {
        launch({
            val newsResponse = newsRepository.getNews()
            articles.addAll(newsResponse?.articles?.toMutableList().orEmpty())
            articleLoaded = newsResponse?.articles?.size ?: 0
            shouldLoadMore = newsResponse?.totalResults ?: 0 > articleLoaded
            success(HomeViewState.InitialDataFetched)
        })
    }

    fun fetchMorePhotos() {
        launch({
            val newsResponse = newsRepository.getNews(++page)
            articles.addAll(newsResponse?.articles?.toMutableList().orEmpty())
            articleLoaded += newsResponse?.articles?.size ?: 0
            shouldLoadMore = newsResponse?.totalResults ?: 0 > articleLoaded
            success(HomeViewState.MoreNewsFetched)
        }, false) {
            page--
            success(HomeViewState.MoreNewsFailed)
        }
    }
}
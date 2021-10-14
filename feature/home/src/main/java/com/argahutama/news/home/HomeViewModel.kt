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
    var photos = mutableListOf<Article>()
        private set

    fun fetchInitialData() {
        launch({
            val newsResponse = newsRepository.getNews()
            photos.addAll(newsResponse?.articles?.toMutableList().orEmpty())
            success(HomeViewState.InitialDataFetched)
        })
    }

    fun fetchMorePhotos() {
        launch({
            val newsResponse = newsRepository.getNews(++page)
            photos.addAll(newsResponse?.articles?.toMutableList().orEmpty())
            success(HomeViewState.MoreNewsFetched)
        }, false) {
            page--
            success(HomeViewState.MoreNewsFailed)
        }
    }
}
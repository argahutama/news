package com.argahutama.news.home

sealed class HomeViewState {
    object InitialDataFetched : HomeViewState()
    object MoreNewsFetched : HomeViewState()
    object MoreNewsFailed : HomeViewState()
}
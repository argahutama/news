package com.argahutama.news.detail

import com.argahutama.news.common.base.BaseViewModel
import com.argahutama.news.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : BaseViewModel<DetailViewState>() {
    var article: Article? = null
}
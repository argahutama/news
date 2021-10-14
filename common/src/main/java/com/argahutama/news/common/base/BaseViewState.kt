package com.argahutama.news.common.base

sealed class BaseViewState<out SuccessState> {
    object Loading : BaseViewState<Nothing>()
    object ConnectionError : BaseViewState<Nothing>()
    object ServerError : BaseViewState<Nothing>()
    data class Error(val throwable: Throwable) : BaseViewState<Nothing>()
    data class Success<out SuccessState>(val data: SuccessState? = null) :
        BaseViewState<SuccessState>()
}
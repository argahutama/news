package com.argahutama.news.common.error

sealed class ErrorViewState {
    object RetrySuccess : ErrorViewState()
    object RetryFailed : ErrorViewState()
    data class RetrySuccessWithError(val throwable: Throwable) : ErrorViewState()
}

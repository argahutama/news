package com.argahutama.news.common.error

import androidx.lifecycle.viewModelScope
import com.argahutama.news.common.base.BaseViewModel
import com.argahutama.news.common.base.BaseViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ErrorViewModel @Inject constructor() : BaseViewModel<ErrorViewState>() {
    val shouldQuit get() = globalRepository.shouldQuitFromError

    fun retry() {
        if (globalRepository.processToRetry == null) {
            success(ErrorViewState.RetrySuccess)
            return
        }
        viewStateMutable.value = BaseViewState.Loading
        viewModelScope.launch(getErrorHandler(globalRepository.processToRetry!!)) {
            try {
                globalRepository.processToRetry?.invoke(this)
                success(ErrorViewState.RetrySuccess)
                globalRepository.processToRetry = null
            } catch (exception: Exception) {
                Timber.i("Error dari generic error")
                exception.printStackTrace()
                val currentState = when {
                    exception is UnknownHostException -> ErrorViewState.RetryFailed
                    exception is SocketTimeoutException -> ErrorViewState.RetryFailed
                    exception.message == "Internal Server Error" -> ErrorViewState.RetryFailed
                    else -> ErrorViewState.RetrySuccessWithError(exception)
                }
                success(currentState)
            }
        }
    }

    override fun getErrorHandler(
        action: suspend CoroutineScope.() -> Unit,
        onError: ((Throwable) -> Unit)?,
    ) = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        success(ErrorViewState.RetryFailed)
    }
}
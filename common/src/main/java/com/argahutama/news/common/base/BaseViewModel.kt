package com.argahutama.news.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argahutama.repository.GlobalRepository
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

abstract class BaseViewModel<SuccessState> : ViewModel() {
    protected val viewStateMutable = MutableLiveData<BaseViewState<SuccessState>>()
    val viewState: LiveData<BaseViewState<SuccessState>>
        get() = viewStateMutable

    @Inject
    lateinit var globalRepository: GlobalRepository

    var launchTime: Long = 0
    var actionTime: Long = 0

    var currentJob: Job? = null
        private set

    protected fun success(successState: SuccessState) {
        viewStateMutable.value = BaseViewState.Success(successState)
    }

    open fun getErrorHandler(
        action: suspend CoroutineScope.() -> Unit,
        onError: ((Throwable) -> Unit)? = null,
    ) = CoroutineExceptionHandler { _, throwable ->
        if (onError == null) {
            globalRepository.processToRetry = action
            handleError(throwable)
        } else {
            throwable.printStackTrace()
            if (throwable !is CancellationException) onError.invoke(throwable)
        }
    }

    protected fun launchConcurrent(
        onError: ((Throwable) -> Unit)? = {
            viewStateMutable.value = BaseViewState.Error(it)
        },
        action: suspend CoroutineScope.() -> Unit,
    ) = innerLaunch(action, onError, false)

    protected fun launch(
        action: suspend CoroutineScope.() -> Unit,
        showLoading: Boolean = true,
        onError: ((Throwable) -> Unit)? = {
            globalRepository.processToRetry = action
            handleError(it)
        },
    ): Job {
        launchTime = System.currentTimeMillis()
        currentJob?.cancel()
        if (showLoading) viewStateMutable.value = BaseViewState.Loading
        currentJob = innerLaunch(action, onError)
        return currentJob!!
    }

    private fun innerLaunch(
        action: suspend CoroutineScope.() -> Unit,
        onError: ((Throwable) -> Unit)? = null,
        trackTime: Boolean = true,
    ) = viewModelScope.launch(getErrorHandler(action, onError)) {
        try {
            action()
            if (trackTime) actionTime = System.currentTimeMillis()
        } catch (e: Throwable) {
            e.printStackTrace()
            if (e !is CancellationException) onError?.invoke(e)
        }
    }

    protected fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
        if (viewState.value == BaseViewState.ConnectionError || viewState.value == BaseViewState.ServerError)
            return
        viewStateMutable.value = when {
            throwable is UnknownHostException -> BaseViewState.ConnectionError
            throwable is SocketTimeoutException -> BaseViewState.ConnectionError
            throwable.message == "Internal Server Error" -> BaseViewState.ServerError
            else -> BaseViewState.Error(throwable)
        }
    }

    fun showConnectionError(action: (suspend CoroutineScope.() -> Unit)? = null) {
        globalRepository.processToRetry = action
        viewStateMutable.value = BaseViewState.ConnectionError
    }
}
package com.argahutama.repository

import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalRepository @Inject constructor() {
    var processToRetry: (suspend CoroutineScope.() -> Unit)? = null

    var shouldQuitFromError = false
}
package com.argahutama.news.common.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.sync.Mutex

object PendingNavigation {
    private val _data = MutableLiveData<MutableList<NavigationPack>>().apply {
        postValue(mutableListOf())
    }
    val data: LiveData<MutableList<NavigationPack>> = _data
    val mutex = Mutex()
    fun add(data: NavigationPack) {
        _data.postValue(_data.value?.apply { add(data) })
    }

    fun remove(data: NavigationPack) {
        _data.postValue(_data.value?.apply { remove(data) })
    }

    fun set(data: NavigationPack) {
        clear()
        add(data)
    }

    fun clear() {
        _data.postValue(_data.value?.apply { clear() })
    }
}
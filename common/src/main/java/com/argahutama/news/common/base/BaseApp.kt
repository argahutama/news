package com.argahutama.news.common.base

import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.argahutama.news.common.navigation.NavigationDirection

abstract class BaseApp : MultiDexApplication() {
    abstract fun navigateTo(context: Context, direction: NavigationDirection)
    abstract fun navigateTo(
        activity: Activity,
        direction: NavigationDirection,
        requestCode: Int
    )

    abstract fun schedule(direction: NavigationDirection, triggerAt: Long)
}
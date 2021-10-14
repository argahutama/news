package com.argahutama.news

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.argahutama.news.common.base.BaseApp
import com.argahutama.news.common.navigation.NavigationDirection
import com.ashokvarma.gander.Gander
import com.ashokvarma.gander.imdb.GanderIMDB
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.Serializable

@HiltAndroidApp
class NewsApp : BaseApp() {
    private val alarmManager by lazy { getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    override fun onCreate() {
        super.onCreate()
        Gander.setGanderStorage(GanderIMDB.getInstance())

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        else Timber.plant(object : Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            }
        })
    }

    override fun schedule(direction: NavigationDirection, triggerAt: Long) {
        val alarmIntent = Intent(this, navigationMapper[direction::class.java]).apply {
            direction.extras.forEach { putExtra(it) }
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, alarmIntent, 0
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    override fun navigateTo(context: Context, direction: NavigationDirection) {
        Intent(context, navigationMapper[direction::class.java]).apply {
            direction.extras.forEach { putExtra(it) }
        }.also { context.startActivity(it) }
    }

    private fun Intent.putExtra(it: Map.Entry<String, Any?>) {
        when (val value = it.value) {
            is Int -> putExtra(it.key, value)
            is Long -> putExtra(it.key, value)
            is CharSequence -> putExtra(it.key, value)
            is String -> putExtra(it.key, value)
            is Float -> putExtra(it.key, value)
            is Double -> putExtra(it.key, value)
            is Char -> putExtra(it.key, value)
            is Short -> putExtra(it.key, value)
            is Boolean -> putExtra(it.key, value)
            is Serializable -> putExtra(it.key, value)
            is Bundle -> putExtra(it.key, value)
            is Parcelable -> putExtra(it.key, value)
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> putExtra(it.key, value)
                value.isArrayOf<String>() -> putExtra(it.key, value)
                value.isArrayOf<Parcelable>() -> putExtra(it.key, value)
                else -> throw Exception("Intent extra ${it.key} has wrong type ${value.javaClass.name}")
            }
            is IntArray -> putExtra(it.key, value)
            is LongArray -> putExtra(it.key, value)
            is FloatArray -> putExtra(it.key, value)
            is DoubleArray -> putExtra(it.key, value)
            is CharArray -> putExtra(it.key, value)
            is ShortArray -> putExtra(it.key, value)
            is BooleanArray -> putExtra(it.key, value)
            else -> throw Exception("Intent extra ${it.key} has wrong type ${value?.javaClass?.name}")
        }
    }

    override fun navigateTo(
        activity: Activity,
        direction: NavigationDirection,
        requestCode: Int,
    ) {
        Intent(activity, navigationMapper[direction::class.java]).apply {
            direction.extras.forEach { putExtra(it) }
        }.also { activity.startActivityForResult(it, requestCode) }
    }
}
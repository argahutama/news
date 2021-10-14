package com.argahutama.news.local.pref

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson

abstract class BasePref(private val gson: Gson, context: Context) {
    abstract val name: String

    private val sharedPref by lazy { context.getSharedPreferences(name, Context.MODE_PRIVATE) }

    fun getString(key: String) = try {
        sharedPref.getString(key, null)
    } catch (_: Exception) {
        clearData(key)
        null
    }

    fun getInt(key: String) = try {
        sharedPref.getInt(key, 0)
    } catch (_: Exception) {
        clearData(key)
        null
    }

    fun getLong(key: String) = try {
        sharedPref.getLong(key, 0L)
    } catch (_: Exception) {
        clearData(key)
        null
    }

    fun getBoolean(key: String) = try {
        sharedPref.getBoolean(key, false)
    } catch (ex: Exception) {
        ex.printStackTrace()
        clearData(key)
        null
    }

    fun <T> getData(key: String, classOfT: Class<T>): T? = try {
        val text = sharedPref.getString(key, null)
        gson.fromJson(text, classOfT)
    } catch (_: Exception) {
        clearData(key)
        null
    }

    fun <T> saveData(key: String, obj: T) =
        sharedPref.edit(true) { putString(key, gson.toJson(obj)) }

    fun saveData(tag: String, value: String) = sharedPref.edit(true) { putString(tag, value) }
    fun saveData(tag: String, value: Int) = sharedPref.edit(true) { putInt(tag, value) }
    fun saveData(tag: String, value: Long) = sharedPref.edit(true) { putLong(tag, value) }
    fun saveData(tag: String, value: Boolean) = sharedPref.edit(true) { putBoolean(tag, value) }

    fun clearData(key: String) = sharedPref.edit(true) { remove(key) }
}
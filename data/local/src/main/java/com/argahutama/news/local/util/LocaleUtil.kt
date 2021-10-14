package com.argahutama.news.local.util

import java.util.*

object LocaleUtil {
    private var locale: Locale? = null
    private val defaultLocale = Locale("in", "ID")

    fun currentLocale(): Locale {
        return locale ?: run {
            locale = defaultLocale
            defaultLocale
        }
    }

    fun getLocaleLanguage() =
        if (locale?.language == "en") "EN"
        else locale?.language ?: "id"
}
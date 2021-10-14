package com.argahutama.news.remote.interceptor

import com.argahutama.news.local.util.LocaleUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AcceptLanguageInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(addHeader(chain.request()))
    }

    private fun addHeader(oriRequest: Request): Request {
        return oriRequest.newBuilder()
            .addHeader("accept-language", LocaleUtil.getLocaleLanguage())
            .build()
    }
}
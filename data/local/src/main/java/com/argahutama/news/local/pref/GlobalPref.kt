package com.argahutama.news.local.pref

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalPref @Inject constructor(
    gson: Gson,
    @ApplicationContext context: Context
) : BasePref(gson, context) {
    override val name = "global_pref"

    companion object {

    }
}
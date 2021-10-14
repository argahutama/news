package com.argahutama.news.common.extension

import android.content.Context
import android.content.Intent
import android.net.Uri

fun String.redirectUrl(context: Context) =
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(this)))
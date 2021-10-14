package com.argahutama.news

import com.argahutama.news.common.error.ConnectionErrorActivity
import com.argahutama.news.common.error.ServerErrorActivity
import com.argahutama.news.common.navigation.NavigationDirection
import com.argahutama.news.detail.DetailActivity
import com.argahutama.news.home.HomeActivity

val navigationMapper = mapOf(
    NavigationDirection.Home::class.java to HomeActivity::class.java,
    NavigationDirection.Detail::class.java to DetailActivity::class.java,
    NavigationDirection.ConnectionError::class.java to ConnectionErrorActivity::class.java,
    NavigationDirection.ServerError::class.java to ServerErrorActivity::class.java,
)
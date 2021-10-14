package com.argahutama.news

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.argahutama.news.databinding.ActivitySplashBinding
import com.argahutama.news.common.base.BaseActivity
import com.argahutama.news.common.navigation.NavigationDirection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashViewState>() {
    override val fullscreen = true
    override val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    override val viewModel by viewModels<SplashViewModel>()
    override fun render(state: SplashViewState?) {}
    override fun setup() = initView()
    private fun initView() {
        lifecycleScope.launch {
            delay(1500)
            finishAffinity()
            navigateTo(NavigationDirection.Home)
        }
    }
}
package com.argahutama.news.common.error

import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.argahutama.news.common.R
import com.argahutama.news.common.base.BaseActivity
import com.argahutama.news.common.databinding.ActivityConnectionErrorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConnectionErrorActivity : BaseActivity<ErrorViewState>() {
    override val binding by lazy { ActivityConnectionErrorBinding.inflate(layoutInflater) }
    override val viewModel by viewModels<ErrorViewModel>()
    override val lightMode = true

    private var backPressed = false
    private var backPressJob: Job? = null

    override fun onBackPressed() {
        if (viewModel.shouldQuit) {
            if (!backPressed) {
                backPressed = true
                backPressJob?.cancel()
                backPressJob = lifecycleScope.launch {
                    delay(2000)
                    backPressed = false
                }
                toast(getString(R.string.press_back_to_exit))
            } else {
                finishAffinity()
            }
        } else super.onBackPressed()
    }

    override fun render(state: ErrorViewState?) {
        if (state is ErrorViewState.RetrySuccess) {
            setResult(RESULT_OK)
            finish()
        } else if (state is ErrorViewState.RetrySuccessWithError) {
            showError(state.throwable)
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun setup() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.grey_100)
        initAction()
    }

    private fun initAction() {
        binding.btnRetry.setOnClickListener { viewModel.retry() }
    }
}
package com.argahutama.news.detail

import androidx.activity.viewModels
import com.argahutama.news.common.base.BaseActivity
import com.argahutama.news.common.extension.redirectUrl
import com.argahutama.news.common.navigation.Extra
import com.argahutama.news.detail.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : BaseActivity<DetailViewState>() {
    override val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    override val viewModel by viewModels<DetailViewModel>()
    override fun render(state: DetailViewState?) {}
    override fun setup() {
        loadArguments()
        initView()
        initAction()
    }

    private fun initView() = with(binding) {
        tvToolbarTitle.text = viewModel.article?.source?.name.orEmpty()
        Glide.with(this@DetailActivity)
            .load(viewModel.article?.urlToImage.orEmpty())
            .into(ivLandscapePoster)
        tvTitle.text = viewModel.article?.title.orEmpty()
        tvUrl.text = viewModel.article?.url.orEmpty()
        tvContent.text = viewModel.article?.content.orEmpty()
    }

    private fun initAction() = with(binding) {
        ivBack.setOnClickListener { onBackPressed() }
        tvUrl.setOnClickListener {
            viewModel.article?.url.orEmpty().redirectUrl(this@DetailActivity)
        }
    }

    private fun loadArguments() {
        viewModel.article = intent.getParcelableExtra(Extra.PHOTO)
    }
}
package com.argahutama.news.home

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.argahutama.news.common.base.BaseActivity
import com.argahutama.news.common.navigation.NavigationDirection
import com.argahutama.news.home.adapter.PhotoGridAdapter
import com.argahutama.news.home.adapter.PhotoListAdapter
import com.argahutama.news.home.databinding.ActivityHomeBinding
import com.argahutama.news.model.Article
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeViewState>() {
    private val photoListAdapter by lazy { PhotoListAdapter(viewModel.articles) }
    private val photoGridAdapter by lazy { PhotoGridAdapter(viewModel.articles) }
    private var backPressed = false
    private var backPressJob: Job? = null
    override val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    override val viewModel by viewModels<HomeViewModel>()
    override fun setup() = viewModel.fetchInitialData()
    override fun render(state: HomeViewState?) {
        when (state) {
            is HomeViewState.InitialDataFetched -> onInitialDataFetched()
            is HomeViewState.MoreNewsFetched -> onMorePhotosFetched()
            is HomeViewState.MoreNewsFailed -> onMorePhotosFailed()
        }
    }

    override fun onBackPressed() = if (!backPressed) {
        backPressed = true
        backPressJob?.cancel()
        backPressJob = lifecycleScope.launch {
            delay(2000)
            backPressed = false
        }
        toast(getString(R.string.press_back_to_exit))
    } else {
        super.onBackPressed()
    }

    private fun initView() = with(binding) {
        rvPhotos.setListView()
        toggleLoadMore(true)
    }

    private fun initAction() = with(binding) {
        ivToolbarList.setOnClickListener { rvPhotos.setListView() }
        ivToolbarGrid.setOnClickListener { rvPhotos.setGridView() }
    }

    private fun onInitialDataFetched() {
        initView()
        initAction()
    }

    private fun onMorePhotosFetched() {
        photoListAdapter.run {
            loadMoreModule.loadMoreComplete()
            setList(viewModel.articles)
        }
        photoGridAdapter.run {
            loadMoreModule.loadMoreComplete()
            setList(viewModel.articles)
        }
        toggleLoadMore(viewModel.shouldLoadMore)
    }

    private fun onMorePhotosFailed() {
        photoListAdapter.loadMoreModule.loadMoreComplete()
        photoGridAdapter.loadMoreModule.loadMoreComplete()
        toggleLoadMore(false)
        lifecycleScope.launch {
            delay(3000L)
            toggleLoadMore(true)
        }
    }

    private fun toggleLoadMore(shouldLoadMore: Boolean) {
        if (shouldLoadMore) {
            photoListAdapter.loadMoreModule.setOnLoadMoreListener { viewModel.fetchMorePhotos() }
            photoGridAdapter.loadMoreModule.setOnLoadMoreListener { viewModel.fetchMorePhotos() }
        } else {
            photoListAdapter.loadMoreModule.setOnLoadMoreListener(null)
            photoGridAdapter.loadMoreModule.setOnLoadMoreListener(null)
        }
        photoListAdapter.loadMoreModule.isEnableLoadMore = shouldLoadMore
        photoGridAdapter.loadMoreModule.isEnableLoadMore = shouldLoadMore
    }

    private fun RecyclerView.setListView() {
        adapter = photoListAdapter.apply {
            setOnItemClickListener { _, _, position ->
                navigateToDetail(viewModel.articles[position])
            }
        }
        layoutManager = LinearLayoutManager(
            this@HomeActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        with(binding) {
            ivToolbarList.setImageResource(R.drawable.ic_round_list_on_24)
            ivToolbarGrid.setImageResource(R.drawable.ic_round_grid_off_24)
        }
    }

    private fun RecyclerView.setGridView() {
        adapter = photoGridAdapter.apply {
            setOnItemClickListener { _, _, position ->
                navigateToDetail(viewModel.articles[position])
            }
        }
        layoutManager = GridLayoutManager(this@HomeActivity, 2)
        with(binding) {
            ivToolbarList.setImageResource(R.drawable.ic_round_list_off_24)
            ivToolbarGrid.setImageResource(R.drawable.ic_round_grid_on_24)
        }
    }

    private fun navigateToDetail(article: Article) = navigateTo(NavigationDirection.Detail(article))
}
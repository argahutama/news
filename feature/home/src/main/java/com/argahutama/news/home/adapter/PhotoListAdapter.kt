package com.argahutama.news.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.argahutama.news.common.base.BaseBindingAdapter
import com.argahutama.news.home.databinding.ItemPhotoListBinding
import com.argahutama.news.model.Article
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class PhotoListAdapter(
    articles: MutableList<Article>
) : BaseBindingAdapter<Article, ItemPhotoListBinding>(articles), LoadMoreModule {
    override fun createBinding(parent: ViewGroup, viewType: Int) =
        ItemPhotoListBinding.inflate(LayoutInflater.from(context), parent, false)

    override fun convert(bd: ItemPhotoListBinding, holder: BaseViewHolder, item: Article) {
        with(bd) {
            Glide.with(context).load(item.urlToImage.orEmpty()).into(ivPhoto)
            tvTitle.text = item.title.orEmpty()
            tvDescription.text = item.description.orEmpty()
        }
    }
}
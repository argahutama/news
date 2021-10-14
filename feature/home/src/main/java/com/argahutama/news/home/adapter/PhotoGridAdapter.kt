package com.argahutama.news.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.argahutama.news.common.base.BaseBindingAdapter
import com.argahutama.news.home.databinding.ItemPhotoGridBinding
import com.argahutama.news.model.Article
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class PhotoGridAdapter(
    articles: MutableList<Article>
) : BaseBindingAdapter<Article, ItemPhotoGridBinding>(articles), LoadMoreModule {
    override fun createBinding(parent: ViewGroup, viewType: Int) =
        ItemPhotoGridBinding.inflate(LayoutInflater.from(context), parent, false)

    override fun convert(bd: ItemPhotoGridBinding, holder: BaseViewHolder, item: Article) {
        Glide.with(context).load(item.urlToImage.orEmpty()).into(bd.ivPhoto)
    }
}
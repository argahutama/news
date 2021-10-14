package com.argahutama.news.common.base

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseBindingAdapter<Item, Binding : ViewBinding>(data: MutableList<Item>) :
    BaseQuickAdapter<Item, BaseBindingAdapter.BindingViewHolder<Binding>>(0, data) {
    private var job: Job? = null

    abstract fun createBinding(parent: ViewGroup, viewType: Int): Binding
    abstract fun convert(bd: Binding, holder: BaseViewHolder, item: Item)

    open fun convertPayloads(bd: Binding, item: Item, payloads: List<Any>) {}

    override fun convert(holder: BindingViewHolder<Binding>, item: Item) {
        convert(holder.bd, holder, item)
    }

    override fun convert(holder: BindingViewHolder<Binding>, item: Item, payloads: List<Any>) {
        if (payloads.isNotEmpty()) convertPayloads(holder.bd, item, payloads)
    }

    override fun onCreateDefViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BindingViewHolder<Binding> {
        return createBaseViewHolder(createBinding(parent, viewType))
    }

    protected open fun createBaseViewHolder(bd: Binding): BindingViewHolder<Binding> {
        return BindingViewHolder(bd)
    }

    open class BindingViewHolder<Binding : ViewBinding>(val bd: Binding) : BaseViewHolder(bd.root)

    fun setOnItemDebouceClickListener(
        coroutineScope: CoroutineScope,
        action: (BaseQuickAdapter<*, *>, View, Int) -> Unit,
    ) {
        setOnItemClickListener { adapter, item, position ->
            job?.cancel()
            job = coroutineScope.launch {
                delay(200L)
                action(adapter, item, position)
            }
        }
    }
}
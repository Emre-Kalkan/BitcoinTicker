package com.emrekalkan.bitcointicker.core

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseAdapter<T, B : ViewDataBinding>(callback: DiffUtil.ItemCallback<T>) : ListAdapter<T, BaseViewHolder<B>>(callback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
        val inflater = LayoutInflater.from(parent.context)
        return BaseViewHolder(createBinding(inflater, parent, viewType))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<B>, position: Int) {
        onBind(holder, position)
    }

    abstract fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): B
    abstract fun onBind(holder: BaseViewHolder<B>, position: Int)
}
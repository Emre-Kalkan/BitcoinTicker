package com.emrekalkan.bitcointicker.ui.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.emrekalkan.bitcointicker.R
import com.emrekalkan.bitcointicker.core.BaseAdapter
import com.emrekalkan.bitcointicker.core.BaseViewHolder
import com.emrekalkan.bitcointicker.data.local.entity.FavouriteCoin
import com.emrekalkan.bitcointicker.databinding.ItemFavouriteCoinsBinding

class FavouriteCoinsListAdapter(
    private val onItemClick: (String) -> Unit
) : BaseAdapter<FavouriteCoin, ItemFavouriteCoinsBinding>(itemCallback) {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemFavouriteCoinsBinding {
        return DataBindingUtil.inflate(inflater, R.layout.item_favourite_coins, parent, false)
    }

    override fun onBind(holder: BaseViewHolder<ItemFavouriteCoinsBinding>, position: Int) {
        holder.binding.apply {
            data = getItem(position)
            adapter = this@FavouriteCoinsListAdapter
            executePendingBindings()
        }
    }

    fun onItemClicked(favouriteCoin: FavouriteCoin) {
        onItemClick(favouriteCoin.id)
    }
}

val itemCallback = object : DiffUtil.ItemCallback<FavouriteCoin>() {
    override fun areItemsTheSame(oldItem: FavouriteCoin, newItem: FavouriteCoin): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FavouriteCoin, newItem: FavouriteCoin): Boolean {
        return oldItem.id == newItem.id
    }
}
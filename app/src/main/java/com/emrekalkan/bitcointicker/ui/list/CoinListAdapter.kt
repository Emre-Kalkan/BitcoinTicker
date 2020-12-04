package com.emrekalkan.bitcointicker.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.emrekalkan.bitcointicker.R
import com.emrekalkan.bitcointicker.core.BaseAdapter
import com.emrekalkan.bitcointicker.core.BaseViewHolder
import com.emrekalkan.bitcointicker.data.remote.coin.CoinItem
import com.emrekalkan.bitcointicker.databinding.ItemCoinBinding

class CoinListAdapter(
    private val onItemClick: (String) -> Unit
) : BaseAdapter<CoinItem, ItemCoinBinding>(itemCallback) {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemCoinBinding {
        return DataBindingUtil.inflate(inflater, R.layout.item_coin, parent, false)
    }

    override fun onBind(holder: BaseViewHolder<ItemCoinBinding>, position: Int) {
        holder.binding.apply {
            data = getItem(position)
            adapter = this@CoinListAdapter
            executePendingBindings()
        }
    }

    fun onItemClicked(coinItem: CoinItem) {
        onItemClick(coinItem.id)
    }
}

val itemCallback = object : DiffUtil.ItemCallback<CoinItem>() {
    override fun areItemsTheSame(oldItem: CoinItem, newItem: CoinItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CoinItem, newItem: CoinItem): Boolean {
        return oldItem.id == newItem.id
    }
}
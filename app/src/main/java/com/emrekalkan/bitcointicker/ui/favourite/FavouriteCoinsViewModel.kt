package com.emrekalkan.bitcointicker.ui.favourite

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emrekalkan.bitcointicker.core.BaseViewModel
import com.emrekalkan.bitcointicker.data.local.entity.FavouriteCoin
import com.emrekalkan.bitcointicker.data.repository.CoinRepository
import com.emrekalkan.bitcointicker.data.repository.FirestoreRepository
import com.emrekalkan.bitcointicker.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouriteCoinsViewModel @ViewModelInject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val coinRepository: CoinRepository
) : BaseViewModel() {

    private val _favouriteCoins = MutableLiveData<Resource<List<FavouriteCoin>>>()
    val favouriteCoins: LiveData<Resource<List<FavouriteCoin>>> = _favouriteCoins

    init {
        fetchFavouriteCoins()
    }

    private fun fetchFavouriteCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            firestoreRepository.fetchFavouriteCoins { resource ->
                viewModelScope.launch(Dispatchers.IO) {
                    if (resource.status == Resource.Status.SUCCESS) {
                        coinRepository.insertFavouriteCoins(resource.data ?: listOf())
                    }
                }
                _favouriteCoins.postValue(resource)
            }
        }
    }

    fun getFavouriteCoinList(): LiveData<List<FavouriteCoin>> {
        return coinRepository.getCoinItemList()
    }
}
package com.emrekalkan.bitcointicker.ui.list

import android.content.SharedPreferences
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emrekalkan.bitcointicker.core.BaseViewModel
import com.emrekalkan.bitcointicker.data.remote.coin.CoinItem
import com.emrekalkan.bitcointicker.data.repository.CoinRepository
import com.emrekalkan.bitcointicker.utils.Resource
import com.emrekalkan.bitcointicker.utils.SharedPrefConst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoinListViewModel @ViewModelInject constructor(
    @Assisted val savedStateHandle: SavedStateHandle,
    private val coinRepository: CoinRepository,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel() {

    private val _coinItemList = MutableLiveData<Resource<List<CoinItem>>>()
    val coinItemList: LiveData<Resource<List<CoinItem>>> = _coinItemList

    var searchQuery: String = ""

    init {
        fetchCoinItemList()
    }

    private fun fetchCoinItemList() {
        viewModelScope.launch(Dispatchers.IO) {
            coinRepository.getCoinItemList {
                _coinItemList.postValue(it)
            }
        }
    }

    fun getCoinItemsByQuery(query: String?) {
        searchQuery = query ?: ""
        viewModelScope.launch(Dispatchers.IO) {
            coinRepository.getCoinItemListByQuery(searchQuery) {
                _coinItemList.postValue(it)
            }
        }
    }

    fun deleteSharedData() {
        sharedPreferences.edit().remove(SharedPrefConst.USER_EMAIL).apply()
    }
}
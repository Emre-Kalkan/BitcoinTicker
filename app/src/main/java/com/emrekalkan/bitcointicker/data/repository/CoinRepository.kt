package com.emrekalkan.bitcointicker.data.repository

import androidx.lifecycle.LiveData
import com.emrekalkan.bitcointicker.data.CoinApi
import com.emrekalkan.bitcointicker.data.local.dao.CoinDao
import com.emrekalkan.bitcointicker.data.local.entity.FavouriteCoin
import com.emrekalkan.bitcointicker.data.remote.coin.CoinDetail
import com.emrekalkan.bitcointicker.data.remote.coin.CoinItem
import com.emrekalkan.bitcointicker.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoinRepository(
    private val coinApi: CoinApi,
    private val coinDao: CoinDao,
    private val firebaseAuthRepository: FirebaseAuthRepository
) {

    fun getCoinItemList(): LiveData<List<FavouriteCoin>> {
        return coinDao.getCoinItemListLiveData(firebaseAuthRepository.getCurrentUser()!!)
    }

    suspend fun getCoinItemList(onResult: (Resource<List<CoinItem>>) -> Unit) {
        coinDao.getCoinItemList().also {
            onResult(Resource.loading(it))
        }
        coinApi.fetchCoinList().also { response ->
            GlobalScope.launch(Dispatchers.IO) {
                if (response.isSuccessful) {
                    (response.body() ?: listOf()).also { coinItemList ->
                        coinDao.deleteCoinList()
                        coinDao.insertCoinList(coinItemList)
                        onResult(Resource.success(coinItemList))
                    }
                } else {
                    onResult(Resource.error(response.errorBody()?.string() ?: "An unexpected error occurred."))
                }
            }

        }
    }

    suspend fun getCoinItemListByQuery(query: String?, onResult: (Resource<List<CoinItem>>) -> Unit) {
        onResult(Resource.loading())
        if (query == null || query.isBlank()) {
            onResult(Resource.success(coinDao.getCoinItemList()))
        } else {
            val mQuery = "%$query%"
            onResult(Resource.success(coinDao.getCoinItemList(mQuery)))
        }
    }

    suspend fun getCoinDetails(id: String, onResult: (Resource<CoinDetail?>) -> Unit) {
        onResult(Resource.loading())
        coinApi.fetchCoinDetail(id).also { response ->
            if (response.isSuccessful) {
                onResult(Resource.success(response.body()))
            } else {
                onResult(Resource.error(response.message()))
            }
        }
    }

    suspend fun getFavouriteCoin(coinId: String): FavouriteCoin? {
        return withContext(Dispatchers.IO) { coinDao.getFavouriteCoin(coinId, firebaseAuthRepository.getCurrentUser()!!) }
    }

    suspend fun deleteFavouriteCoin(coinId: String) {
        coinDao.deleteFavouriteCoin(coinId, firebaseAuthRepository.getCurrentUser()!!)
    }

    suspend fun addToFavourites(coin: FavouriteCoin) {
        coinDao.insertFavouriteCoin(coin)
    }

    suspend fun getFavouriteCoins(): List<FavouriteCoin> {
        return withContext(Dispatchers.IO) { coinDao.getFavouriteCoins(firebaseAuthRepository.getCurrentUser()!!) }
    }

    suspend fun insertFavouriteCoins(list: List<FavouriteCoin>) {
        coinDao.insertFavouriteCoins(list)
    }
}
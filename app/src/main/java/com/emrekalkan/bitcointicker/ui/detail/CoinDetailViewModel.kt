package com.emrekalkan.bitcointicker.ui.detail

import androidx.databinding.ObservableField
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emrekalkan.bitcointicker.core.BaseViewModel
import com.emrekalkan.bitcointicker.data.local.entity.FavouriteCoin
import com.emrekalkan.bitcointicker.data.remote.coin.CoinDetail
import com.emrekalkan.bitcointicker.data.repository.CoinRepository
import com.emrekalkan.bitcointicker.data.repository.FirebaseAuthRepository
import com.emrekalkan.bitcointicker.data.repository.FirestoreRepository
import com.emrekalkan.bitcointicker.utils.DateConst
import com.emrekalkan.bitcointicker.utils.Resource
import com.emrekalkan.bitcointicker.utils.ext.toString
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class CoinDetailViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val coinRepository: CoinRepository,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : BaseViewModel() {

    private val _coinDetails = MutableLiveData<Resource<CoinDetail?>>()
    val coinDetails: LiveData<Resource<CoinDetail?>> = _coinDetails

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    private val _favouriteOperation = MutableLiveData<Resource<Task<Void>>>()
    val favouriteOperation: LiveData<Resource<Task<Void>>> = _favouriteOperation

    var isFavouriteCoinInProgress = false
    var isPriceRefreshActive = false
    var priceRefreshInterval: Int = -1

    val coinId = savedStateHandle.get<String>("id")!!
    val lastUpdated = ObservableField("")
    val minIntervalInSeconds: Int = 5

    init {
        fetchCoinDetails()
        checkIsFavouriteFromRoom()
    }

    private fun fetchCoinDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            coinRepository.getCoinDetails(coinId) {
                _coinDetails.postValue(it)
                setObservableFields(it.data)
            }
        }
    }

    private fun checkIsFavouriteFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            coinRepository.getFavouriteCoin(coinId).also { favouriteCoin ->
                _isFavourite.postValue(favouriteCoin != null)
            }
        }
    }

    private fun setObservableFields(coinDetail: CoinDetail?) {
        lastUpdated.set(getLastUpdatedString(coinDetail))
    }

    private fun getLastUpdatedString(coinDetail: CoinDetail?): String {
        return coinDetail?.marketData?.lastUpdated?.toString(DateConst.HOUR_MINUTE_SECOND).run {
            if (this?.isNotBlank() == true) {
                "Last updated: $this"
            } else {
                ""
            }
        }
    }

    fun relaunchPriceRefreshCoroutineIfActive() {
        if (isPriceRefreshActive) {
            launchPriceRefreshCoroutine()
        }
    }

    fun launchPriceRefreshCoroutine() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Price refresh coroutine launched")
            if (isPriceRefreshActive) {
                Timber.d("Price refresh coroutine is active")
                delay(priceRefreshInterval.times(1000).toLong())
                fetchCoinDetails()
                Timber.d("Price refresh coroutine is started again")
            } else {
                Timber.d("Price refresh coroutine is not active")
                return@launch
            }
        }
    }

    fun addOrRemoveAsFavouriteCoin() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isFavouriteCoinInProgress.not()) {
                isFavouriteCoinInProgress = true
                isFavourite.value?.also { isFavourite ->
                    coinDetails.value?.data?.also { coin ->
                        if (isFavourite.not()) {
                            val favouriteCoin = FavouriteCoin(
                                id = coin.id,
                                userId = firebaseAuthRepository.getCurrentUser()!!,
                                name = coin.name,
                                symbol = coin.symbol
                            )
                            firestoreRepository.addFavouriteCoin(favouriteCoin) { resource ->
                                _favouriteOperation.postValue(resource)
                                if (resource.data?.isSuccessful == true) {
                                    viewModelScope.launch(Dispatchers.IO) { coinRepository.addToFavourites(favouriteCoin) }
                                    _isFavourite.postValue(true)
                                }
                            }
                        } else {
                            firestoreRepository.removeFavouriteCoin(coin.id) { resource ->
                                _favouriteOperation.postValue(resource)
                                if (resource.data?.isSuccessful == true) {
                                    viewModelScope.launch(Dispatchers.IO) { coinRepository.deleteFavouriteCoin(coin.id) }
                                    _isFavourite.postValue(false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
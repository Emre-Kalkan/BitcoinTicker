package com.emrekalkan.bitcointicker.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emrekalkan.bitcointicker.data.local.entity.FavouriteCoin
import com.emrekalkan.bitcointicker.data.remote.coin.CoinItem

@Dao
interface CoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinList(coinList: List<CoinItem>)

    @Query("SELECT * FROM CoinItem")
    suspend fun getCoinItemList(): List<CoinItem>

    @Query("SELECT * FROM CoinItem WHERE name LIKE :query OR symbol LIKE :query")
    suspend fun getCoinItemList(query: String?): List<CoinItem>

    @Query("DELETE FROM CoinItem")
    suspend fun deleteCoinList()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteCoin(favouriteCoin: FavouriteCoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteCoins(list: List<FavouriteCoin>)

    @Query("SELECT * FROM FavouriteCoin WHERE userId=:userId")
    suspend fun getFavouriteCoins(userId: String): List<FavouriteCoin>

    @Query("DELETE FROM FavouriteCoin WHERE id=:coinId AND userId=:userId")
    suspend fun deleteFavouriteCoin(coinId: String, userId: String)

    @Query("SELECT * FROM FavouriteCoin WHERE id=:coinId AND userId=:userId")
    suspend fun getFavouriteCoin(coinId: String, userId: String): FavouriteCoin?

    @Query("SELECT * FROM FavouriteCoin WHERE userId=:userId")
    fun getCoinItemListLiveData(userId: String): LiveData<List<FavouriteCoin>>
}
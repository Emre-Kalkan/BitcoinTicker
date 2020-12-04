package com.emrekalkan.bitcointicker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emrekalkan.bitcointicker.data.local.dao.CoinDao
import com.emrekalkan.bitcointicker.data.local.entity.FavouriteCoin
import com.emrekalkan.bitcointicker.data.remote.coin.CoinItem

@Database(version = 1, entities = [CoinItem::class, FavouriteCoin::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinListDao(): CoinDao
}
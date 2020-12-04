package com.emrekalkan.bitcointicker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.emrekalkan.bitcointicker.data.local.AppDatabase
import com.emrekalkan.bitcointicker.data.local.dao.CoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "BitcoinTickerDB"
        ).build()
    }

    @Singleton
    @Provides
    fun provideCoinDao(appDatabase: AppDatabase): CoinDao {
        return appDatabase.coinListDao()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("CommonSharedPreferences", Context.MODE_PRIVATE)
    }

}
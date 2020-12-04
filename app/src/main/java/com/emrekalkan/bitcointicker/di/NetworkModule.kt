package com.emrekalkan.bitcointicker.di

import android.os.Environment
import com.emrekalkan.bitcointicker.data.CoinApi
import com.emrekalkan.bitcointicker.utils.DateConst
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat(DateConst.DATE_TIME_OFFSET)
            .create()
    }

    @Singleton
    @Provides
    fun provideClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val cache = Cache(Environment.getDownloadCacheDirectory(), 10 * 1024 * 1024)
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideService(builder: Retrofit.Builder): CoinApi {
        return builder
            .baseUrl("https://api.coingecko.com/api/v3/")
            .build()
            .create(CoinApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }
}
package com.emrekalkan.bitcointicker.data

import com.emrekalkan.bitcointicker.data.remote.coin.CoinDetail
import com.emrekalkan.bitcointicker.data.remote.coin.CoinItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApi {

    @GET("coins/list")
    suspend fun fetchCoinList(): Response<List<CoinItem>>

    @GET("coins/{id}")
    suspend fun fetchCoinDetail(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("sparkline") sparkLine: Boolean = false
    ): Response<CoinDetail>
}
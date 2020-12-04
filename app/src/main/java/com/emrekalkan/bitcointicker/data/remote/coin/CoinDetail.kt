package com.emrekalkan.bitcointicker.data.remote.coin

import com.google.gson.annotations.SerializedName
import java.util.*

data class CoinDetail(
    val id: String,
    val symbol: String,
    val name: String,
    @SerializedName("hashing_algorithm")
    val hashingAlgorithm: String,
    val description: CoinLocalization,
    val image: CoinImages,
    @SerializedName("market_data")
    val marketData: CoinMarketData
)

data class CoinMarketData(
    @SerializedName("current_price")
    val currentPrice: CoinCurrencies,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double,
    @SerializedName("last_updated")
    val lastUpdated: Date
)

data class CoinImages(
    val thumb: String,
    val small: String,
    val large: String
)

data class CoinLocalization(
    val en: String = ""
)

data class CoinCurrencies(
    val eur: Double,
    val usd: Double,
    @SerializedName("try")
    val trTry: Double,
    val btc: Double
)

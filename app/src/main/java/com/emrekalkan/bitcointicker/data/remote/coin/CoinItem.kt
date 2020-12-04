package com.emrekalkan.bitcointicker.data.remote.coin

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "CoinItem")
data class CoinItem(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("name")
    val name: String
)
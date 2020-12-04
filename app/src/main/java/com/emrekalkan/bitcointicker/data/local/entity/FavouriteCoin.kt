package com.emrekalkan.bitcointicker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavouriteCoin")
data class FavouriteCoin(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val symbol: String
)
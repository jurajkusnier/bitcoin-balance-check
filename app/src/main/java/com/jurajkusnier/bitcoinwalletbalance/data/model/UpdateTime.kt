package com.jurajkusnier.bitcoinwalletbalance.data.model

import androidx.room.TypeConverters
import com.jurajkusnier.bitcoinwalletbalance.data.db.DateConverter
import java.util.*

@TypeConverters(DateConverter::class)
data class UpdateTime(private val lastUpdate: Date) {
    fun isOlderThan(timeInMs: Long): Boolean {
        return Date().time - lastUpdate.time > timeInMs
    }
}
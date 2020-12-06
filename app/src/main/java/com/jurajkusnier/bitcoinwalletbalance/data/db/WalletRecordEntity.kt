package com.jurajkusnier.bitcoinwalletbalance.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jurajkusnier.bitcoinwalletbalance.data.api.OneTransaction
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

@Entity(tableName = "WalletRecord")
@OptIn(ExperimentalTime::class)
data class WalletRecordEntity(
    @PrimaryKey val address: String,
    val nickname: String,
    val lastAccess: Long,
    val lastUpdate: Long,
    val showInHistory: Boolean,
    val favourite: Boolean,
    val totalReceived: Long,
    val totalSent: Long,
    val finalBalance: Long
) {

    fun isOld() = (System.currentTimeMillis() - lastUpdate) > 15.minutes.toLongMilliseconds()

    companion object {
        fun fromWalletRecord(record: WalletRecord) = WalletRecordEntity(
            record.address,
            record.nickname,
            record.lastAccess,
            record.lastUpdate,
            true,
            record.favourite,
            record.totalReceived,
            record.totalSent,
            record.finalBalance
        )
    }

    fun toWalletRecord(transactions: List<OneTransaction>?): WalletRecord {
        return WalletRecord(
            address = address,
            nickname = nickname,
            lastAccess = lastAccess,
            lastUpdate = lastUpdate,
            showInHistory = showInHistory,
            favourite = favourite,
            totalReceived = totalReceived,
            totalSent = totalSent,
            finalBalance = finalBalance,
            transactions = transactions ?: emptyList()
        )
    }
}

data class WalletRecord(
    val address: String,
    val nickname: String = "",
    val lastAccess: Long,
    val lastUpdate: Long,
    val showInHistory: Boolean = true,
    val favourite: Boolean = false,
    val totalReceived: Long,
    val totalSent: Long,
    val finalBalance: Long,
    val transactions: List<OneTransaction>
)

sealed class WalletRecordAction {
    object LoadingStart : WalletRecordAction()
    data class LoadingEnd(val success: Boolean) : WalletRecordAction()
    data class ItemUpdated(val value: WalletRecord) : WalletRecordAction()
}
package com.jurajkusnier.bitcoinwalletbalance.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jurajkusnier.bitcoinwalletbalance.data.model.OneTransaction
import com.jurajkusnier.bitcoinwalletbalance.data.model.RepositoryResponse

@Entity(tableName = "WalletRecord")
data class WalletRecordEntity(
        @PrimaryKey val address: String,
        val nickname: String,
        val lastAccess: Long,
        val lastUpdate: Long,
        val showInHistory: Boolean,
        val favourite: Boolean,
        val totalReceived: Long,
        val totalSent: Long,
        val finalBalance: Long) {

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
                record.finalBalance)
    }

    fun toWalletRecord(transactions: List<OneTransaction>): WalletRecord {
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
                transactions = transactions)
    }
}

data class WalletRecord(
        val address: String,
        val nickname: String,
        val lastAccess: Long,
        val lastUpdate: Long,
        val showInHistory: Boolean,
        val favourite: Boolean,
        val totalReceived: Long,
        val totalSent: Long,
        val finalBalance: Long,
        val transactions: List<OneTransaction>)


data class OptionalWalletRecord(val source: RepositoryResponse.Source, val value: WalletRecord?) {
}

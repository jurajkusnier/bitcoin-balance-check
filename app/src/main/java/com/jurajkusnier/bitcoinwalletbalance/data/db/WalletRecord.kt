package com.jurajkusnier.bitcoinwalletbalance.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.jurajkusnier.bitcoinwalletbalance.data.model.OneTransaction

@Entity
data class WalletRecord(
        @PrimaryKey val address:String,
        val nickname:String,

        val lastAccess:Long,
        val lastUpdate:Long,

        val showInHistory:Boolean,
        val favourite:Boolean,

        val totalReceived:Long,
        val totalSent:Long,
        val finalBalance:Long)

data class WalletRecordView(
        val address:String,
        val nickname: String,

        val lastAccess:Long,
        val lastUpdate:Long,

        val showInHistory:Boolean,
        val favourite:Boolean,

        val totalReceived:Long,
        val totalSent:Long,
        val finalBalance:Long,

        val transactions:Array<OneTransaction>,
        val fromCache: Boolean)


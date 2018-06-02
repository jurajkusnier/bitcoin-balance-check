package com.jurajkusnier.bitcoinwalletbalance.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class WalletRecord(
        @PrimaryKey val address:String,
        var lastAccess:Long,
        var showInHistory:Boolean,
        var favourite:Boolean,
        var satoshis:Long)

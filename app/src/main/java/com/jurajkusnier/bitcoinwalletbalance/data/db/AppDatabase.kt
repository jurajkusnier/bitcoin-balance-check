package com.jurajkusnier.bitcoinwalletbalance.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WalletRecordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletRecordDao(): AppDatabaseDao
}
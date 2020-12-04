package com.jurajkusnier.bitcoinwalletbalance.ui.main

import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabaseDao
import javax.inject.Inject

class ListRepository @Inject constructor(private val databaseDao: AppDatabaseDao) {

    fun getAll() = databaseDao.getAll()

}
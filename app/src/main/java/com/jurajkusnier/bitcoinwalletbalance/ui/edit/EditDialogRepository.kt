package com.jurajkusnier.bitcoinwalletbalance.ui.edit

import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabaseDao
import javax.inject.Inject

class EditDialogRepository @Inject constructor(private val databaseDao: AppDatabaseDao) {

    suspend fun setNickname(address: String, nickname: String) {
        databaseDao.setNickName(address, nickname)
    }

}
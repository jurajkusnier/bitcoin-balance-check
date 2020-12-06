package com.jurajkusnier.bitcoinwalletbalance.ui.main

import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity

interface ListAdapterActions {
    fun onOpen(address: String)
    fun onEdit(item: WalletRecordEntity)
    fun onToggleFavourite(item: WalletRecordEntity)
    fun onDelete(address: String)
}
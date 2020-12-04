package com.jurajkusnier.bitcoinwalletbalance.ui.main

interface ListAdapterActions {
    fun onOpen(address:String)
    fun onEdit(address:String)
    fun onToggleFavourite(address: String)
    fun onDelete(address: String)
}
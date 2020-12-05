package com.jurajkusnier.bitcoinwalletbalance.ui.main

import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity

class FavouriteFragment : ListFragment() {

    override fun getEmptyText(): String = getString(R.string.favourites_list_is_empty)

    override fun filter(item: WalletRecordEntity): Boolean = item.favourite

}
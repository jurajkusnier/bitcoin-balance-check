package com.jurajkusnier.bitcoinwalletbalance.ui.main;

import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity

class HistoryFragment : ListFragment() {

    override fun getEmptyText(): String = getString(R.string.history_is_empty)

    override fun filter(item: WalletRecordEntity): Boolean = true

}

package com.jurajkusnier.bitcoinwalletbalance.ui.main;

import com.jurajkusnier.bitcoinwalletbalance.R

class HistoryFragment : ListFragment() {

    override fun getEmptyText(): String = getString(R.string.history_is_empty)

}

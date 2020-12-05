package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.view.View
import kotlinx.android.synthetic.main.detail_fragment.view.*

class TransactionsComponent(private val view: View, private val listAdapter: TransactionListAdapter) {

    init {
        view.recyclerViewTransactions.apply {
            adapter = listAdapter
            recyclerViewTransactions?.isNestedScrollingEnabled = false
        }
    }

    fun bind(state: WalledDetailsViewState) {
        state.wallet?.let {
            listAdapter.submitList(it.transactions)
        }
        if ((state.wallet == null || state.wallet.transactions.isEmpty()) && !state.loading) {
            view.textViewNoTransaction.visibility = View.VISIBLE
        } else {
            view.textViewNoTransaction.visibility = View.GONE
        }
    }
}
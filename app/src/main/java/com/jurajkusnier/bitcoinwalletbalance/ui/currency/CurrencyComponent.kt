package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import kotlinx.android.synthetic.main.currency_bottomsheet_layout.view.*

class CurrencyComponent(private val view: View, private val onCurrencyCodeSelected: (CurrencyCode) -> Unit) {

    init {
        view.currencyRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(view.context)
            adapter = CurrencyAdapter(onCurrencyCodeSelected)
            itemAnimator = null
        }
    }

    fun bind(currencyListItems: List<CurrencyItem>) {
        (view.currencyRecyclerView.adapter as CurrencyAdapter).submitList(currencyListItems)
    }
}
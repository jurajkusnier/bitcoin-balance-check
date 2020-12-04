package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.content.Context
import android.view.View
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.utils.sathoshiToBTCstring
import kotlinx.android.synthetic.main.detail_fragment.view.*

class DetailInfoComponent(private val view: View) {

    fun bind(state: WalledDetailsViewState) {
        val btcString = sathoshiToBTCstring(state.wallet?.finalBalance ?: 0)
        view.apply {
            textFinalBalanceCrypto.text = btcString
            newTitle.text = btcString
            textTotalReceived.text = sathoshiToBTCstring(state.wallet?.totalReceived ?: 0)
            textTotalSent.text = sathoshiToBTCstring(state.wallet?.totalSent ?: 0)
            textFinalBalanceMoney.text = getFormattedFiat(view.context, state)
        }
    }

    private fun getFormattedFiat(
        context: Context,
        state: WalledDetailsViewState
    ): String {
        val btcValue = (state.wallet?.finalBalance ?: 0) / 100_000_000.00
        val exchangeRate = state.exchangeRate?.rate ?: return ""
        val currencyCode = state.currencyCode ?: return ""
        val fiat = btcValue * exchangeRate
        return context.getString(R.string.btc_price_in_fiat, fiat, currencyCode)
    }
}
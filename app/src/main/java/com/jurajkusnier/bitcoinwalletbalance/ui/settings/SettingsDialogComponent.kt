package com.jurajkusnier.bitcoinwalletbalance.ui.settings

import android.R.layout
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRate
import kotlinx.android.synthetic.main.settings_dialog_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class SettingsDialogComponent(private val view: View, private val onCurrencyCodeSelected: (CurrencyCode) -> Unit) {

    fun bind(currencyViewState: CurrencyViewState) {
        Log.d(TAG, "observe(): $currencyViewState")

        if (currencyViewState.loading) {
            showLoading()
        } else {
            showData(currencyViewState)
        }
    }

    private fun setupSpinner(currencyCodes: List<CurrencyCode>, exchangeRates: Map<CurrencyCode, ExchangeRate>, currencyCode: CurrencyCode?) {
        view.spinnerCurrency.apply {
            adapter = ArrayAdapter(view.context, layout.simple_spinner_item, currencyCodes)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedCurrencyCode = currencyCodes[position]
                    exchangeRates[selectedCurrencyCode]?.let { exchangeRate ->
                        showSelectedCurrency(selectedCurrencyCode, exchangeRate)
                    }
                    onCurrencyCodeSelected(selectedCurrencyCode)
                }
            }
            currencyCode?.let {
                setSelection(currencyCodes.indexOfFirst { it == currencyCode },false)
            }
        }
    }

    private fun showLoading() {
        view.progressLoaderSettings.visibility = View.VISIBLE
        view.textViewExchangeError.visibility = View.VISIBLE
    }

    private fun showData(currencyViewState: CurrencyViewState) {
        view.progressLoaderSettings.visibility = View.GONE
        view.textViewExchangeError.visibility = if (currencyViewState.connectionError) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        if (currencyViewState.data != null) {
            view.textViewBTCtoUSDupdate.text = getLastUpateString(currencyViewState.data.lastUpdate)
            setupSpinner(currencyCodes = currencyViewState.data.values.keys.toList(),
                    exchangeRates = currencyViewState.data.values,
                    currencyCode = currencyViewState.currencyCode)
        }
    }

    private fun showSelectedCurrency(currencyCode: CurrencyCode, exchangeRate: ExchangeRate) {
        view.textViewBTCtoUSD.text = view.resources.getString(com.jurajkusnier.bitcoinwalletbalance.R.string.BTC_to_currency, exchangeRate.rate, currencyCode)
    }

    private fun getLastUpateString(date: Date): String {
        if (date.time == 0L) return ""
        val dateFormat = view.resources.getString(com.jurajkusnier.bitcoinwalletbalance.R.string.date_format)
        val sdf = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        return sdf.format(date)
    }

    companion object {
        const val TAG = "SettingsDialogComponent"
    }
}
package com.jurajkusnier.bitcoinwalletbalance.ui.settings

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.ConversionPrefs
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.settings_dialog_layout.view.*
import javax.inject.Inject

class SettingsDialog: DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    companion object {
        val TAG = SettingsDialog::class.java.simpleName
    }

    private var _view:View? = null

    private lateinit var settingViewModel:SettingViewModel

    var gotAtLeastCachedData = false
    var selectedCurrencyCode = ""


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val arrayAdapter = ArrayAdapter(context,android.R.layout.simple_spinner_item, ConversionPrefs.currencyCodes)

        val builder = AlertDialog.Builder(context!!, R.style.AppCompatAlertDialogStyle)
        // Get the layout inflater
        val inflater = activity?.layoutInflater
        _view = inflater?.inflate(R.layout.settings_dialog_layout, null)
        _view?.progressLoaderSettings?.visibility = View.GONE
        _view?.spinnerCurrency?.adapter = arrayAdapter


        builder.setTitle(getString(R.string.settings))
        builder.setView(_view)
                // Add action buttons
                .setNegativeButton(getString(R.string.close), { _, _ ->

                })
                .setPositiveButton(getString(R.string.save), { _, _ ->
                    val newCurrencyId =_view?.spinnerCurrency?.selectedItemPosition
                    if (newCurrencyId != null) {
                        settingViewModel.changeCurrency(newCurrencyId)
                    }
                })


        settingViewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingViewModel::class.java)

        settingViewModel.loadingState.observe(this, Observer {
            if (it == SettingViewModel.LoadingState.LOADING) {
                _view?.progressLoaderSettings?.visibility = View.VISIBLE
            } else {
                _view?.progressLoaderSettings?.visibility = View.GONE
            }

            if (it == SettingViewModel.LoadingState.ERROR) {
                _view?.textViewExchangeError?.visibility = View.VISIBLE
            } else {
                _view?.textViewExchangeError?.visibility = View.INVISIBLE
            }

            if (it == SettingViewModel.LoadingState.ERROR && !gotAtLeastCachedData) {
                _view?.textViewBTCtoUSD?.text = "1 BTC = N/A $selectedCurrencyCode"
                _view?.textViewBTCtoUSDupdate?.visibility = View.INVISIBLE
                _view?.textViewExchangeError?.visibility = View.VISIBLE
            }
        })

        settingViewModel.currencyRate.observe(this, Observer {
            _view?.textViewBTCtoUSD?.text = it?.exchangeRate
            _view?.textViewBTCtoUSDupdate?.visibility = View.VISIBLE
            _view?.textViewBTCtoUSDupdate?.text = it?.lastUpdate
            gotAtLeastCachedData = true

        })

        val id = settingViewModel.getCurrencyId()
        _view?.spinnerCurrency?.setSelection(id)


        _view?.spinnerCurrency?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCurrencyCode = _view?.spinnerCurrency?.selectedItem.toString().toUpperCase()
                gotAtLeastCachedData = false
                settingViewModel.changeCurrencyPreview(selectedCurrencyCode )
            }
        }

        settingViewModel.changeCurrencyPreview()

        return builder.create()
    }
}
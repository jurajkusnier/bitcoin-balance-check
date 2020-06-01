package com.jurajkusnier.bitcoinwalletbalance.ui.settings

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class SettingsDialog : DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var settingsDialogComponent: SettingsDialogComponent

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        settingViewModel = ViewModelProvider(this, viewModelFactory).get(SettingViewModel::class.java)

        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.settings_dialog_layout, null) ?: throw NullPointerException("Can't inflate layout")

        settingsDialogComponent = SettingsDialogComponent(view, settingViewModel::selectCurrencyCode)
        
        val builder = AlertDialog.Builder(requireContext(), R.style.AppCompatAlertDialogStyle)
        builder.setTitle(R.string.settings)
        builder.setView(view)
        builder.setPositiveButton(R.string.save) { _, _ ->
            settingViewModel.setCurrencyCode()
        }

        val dialog = builder.create()

        settingViewModel.currencyViewState.observe( requireActivity(), Observer { currencyViewState ->
            settingsDialogComponent.bind(currencyViewState)
        })

        return dialog
    }

    companion object {
        const val TAG = "SettingsDialog"
    }
}
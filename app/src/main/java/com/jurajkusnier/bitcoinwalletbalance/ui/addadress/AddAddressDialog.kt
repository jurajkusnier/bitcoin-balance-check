package com.jurajkusnier.bitcoinwalletbalance.ui.addadress

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.add_address_dialog_layout.view.*

@AndroidEntryPoint
class AddAddressDialog : AppCompatDialogFragment() {

    private val dialogViewModel: AddAddressDialogViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels({ requireActivity() })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.add_address_dialog_layout, null)
        view?.buttonAddClipboard?.setOnClickListener {
            val addressFromClipboard = dialogViewModel.getClipboardText()
            if (addressFromClipboard != null) {
                view.editTextWalletAddress?.setText(addressFromClipboard)
            } else {
                Toast.makeText(context, getString(R.string.clipboard_is_empty), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return AlertDialog.Builder(
            requireContext(),
            R.style.ThemeOverlay_MaterialComponents_Dialog_Alert
        )
            .setTitle(getString(R.string.add_address))
            .setView(view)
            .setNegativeButton(getString(R.string.close)) { _, _ -> }
            .setPositiveButton(getString(R.string.add)) { _, _ ->
                val bitcoinAddress = view?.editTextWalletAddress?.text.toString()
                if (bitcoinAddress.isNotBlank()) {
                    dismiss()
                    mainViewModel.openDetails(bitcoinAddress)
                }
            }
            .create()
    }

    companion object {
        private const val TAG = "AddAddressDialog"

        fun show(fragmentManager: FragmentManager) {
            AddAddressDialog().show(fragmentManager, TAG)
        }
    }
}
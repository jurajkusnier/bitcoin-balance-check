package com.jurajkusnier.bitcoinwalletbalance.ui.addadress

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import android.widget.Toast
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.detail.DetailFragment
import kotlinx.android.synthetic.main.add_address_dialog_layout.view.*


class AddAddressDialog: AppCompatDialogFragment() {

    private lateinit var dialogViewModel: AddAddressDialogViewModel

    private var _view: View? = null

    companion object {
        val TAG = AddAddressDialog::class.java.simpleName

        fun newInstance():AddAddressDialog {
            val dialog = AddAddressDialog()
            return dialog
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        dialogViewModel= ViewModelProviders.of(this).get(AddAddressDialogViewModel::class.java)

        val builder = AlertDialog.Builder(context!!, R.style.AppCompatAlertDialogStyle)

        // Get the layout inflater
        val inflater = activity?.layoutInflater
        _view = inflater?.inflate(R.layout.add_address_dialog_layout, null)
        builder.setTitle(getString(R.string.add_address))
        builder.setView(_view)
                // Add action buttons
                .setNegativeButton(getString(R.string.close)) { _, _ ->

                }
                .setPositiveButton(getString(R.string.add)) { _, _ ->

                    val bitcoinAddress = _view?.editTextWalletAddress?.text.toString()

                    if (bitcoinAddress.isNotBlank()) {
                        activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.container, DetailFragment.newInstance(bitcoinAddress))
                                ?.addToBackStack(DetailFragment.TAG)
                                ?.commit()
                    }
                }

        _view?.buttonAddClipboard?.setOnClickListener {
            val addressFromClipboard = dialogViewModel.getClipboardText()
            if (addressFromClipboard != null) {
                _view?.editTextWalletAddress?.setText(addressFromClipboard)
            } else {
                Toast.makeText(context,getString(R.string.clipboard_is_empty),Toast.LENGTH_SHORT).show()
            }
        }

        return builder.create()
    }
}
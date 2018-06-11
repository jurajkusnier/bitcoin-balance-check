package com.jurajkusnier.bitcoinwalletbalance.ui.edit

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.edit_dialog_layout.view.*
import javax.inject.Inject

class EditDialog: DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var editDialogViewModel: EditDialogViewModel

    var _view: View? = null

    companion object {
        val TAG = EditDialog::class.java.simpleName
        private val BUNDLE_ADDRESS = "address"
        private val BUNDLE_NICKNAME = "nickname"

        fun newInstance(address:String,nickname:String):EditDialog {

            val dialog = EditDialog()

            val args = Bundle()
            args.putString(BUNDLE_ADDRESS,address)
            args.putString(BUNDLE_NICKNAME,nickname)
            dialog.arguments = args

            return dialog
        }
    }

    var address:String? = null
    var nickname:String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        editDialogViewModel = ViewModelProviders.of(this, viewModelFactory).get(EditDialogViewModel::class.java)

        val builder = AlertDialog.Builder(context!!, R.style.AppCompatAlertDialogStyle)
        // Get the layout inflater
        val inflater = activity?.layoutInflater
        _view = inflater?.inflate(R.layout.edit_dialog_layout, null)
        builder.setTitle(getString(R.string.edit))
        builder.setView(_view)
                // Add action buttons
                .setNegativeButton(getString(R.string.close), { _, _ ->

                })
                .setPositiveButton(getString(R.string.save), { _, _ ->
                    address?.let {
                        val newNickname = _view?.editTextWalletNickname?.text.toString()
                        editDialogViewModel.setNickname(it, newNickname)
                    }
                })

        address = arguments?.getString(BUNDLE_ADDRESS)
        nickname = arguments?.getString(BUNDLE_NICKNAME)

        _view?.textWalletAddress?.text = address
        _view?.editTextWalletNickname?.setText(nickname)

        return builder.create()
    }
}
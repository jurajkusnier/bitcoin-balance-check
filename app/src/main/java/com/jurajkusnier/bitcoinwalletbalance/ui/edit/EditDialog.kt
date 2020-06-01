package com.jurajkusnier.bitcoinwalletbalance.ui.edit

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.edit_dialog_layout.view.*
import javax.inject.Inject

class EditDialog : DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var editDialogViewModel: EditDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        editDialogViewModel = ViewModelProvider(this, viewModelFactory).get(EditDialogViewModel::class.java)

        val view = View.inflate(context, R.layout.edit_dialog_layout, null)

        view.textWalletAddress.text = arguments?.getString(BUNDLE_ADDRESS)
        view.editTextWalletNickname.setText(arguments?.getString(BUNDLE_NICKNAME))

        return AlertDialog.Builder(requireContext(), R.style.AppCompatAlertDialogStyle)
                .setTitle(getString(R.string.edit))
                .setView(view)
                .setNegativeButton(getString(R.string.close)) { _, _ -> }
                .setPositiveButton(getString(R.string.save)) { _, _ ->
                    arguments?.getString(BUNDLE_ADDRESS)?.let {
                        val newNickname = view.editTextWalletNickname.text.toString()
                        editDialogViewModel.setNickname(it, newNickname)
                    }
                }.create()
    }

    companion object {
        const val TAG = "EditDialog"
        private const val BUNDLE_ADDRESS = "address"
        private const val BUNDLE_NICKNAME = "nickname"

        fun newInstance(parameters: Parameters): EditDialog {
            return EditDialog().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_ADDRESS, parameters.address)
                    putString(BUNDLE_NICKNAME, parameters.nickname)
                }
            }
        }
    }

    data class Parameters(val address: String, val nickname: String)
}
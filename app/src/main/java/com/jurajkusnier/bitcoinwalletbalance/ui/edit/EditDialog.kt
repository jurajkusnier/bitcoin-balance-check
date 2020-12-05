package com.jurajkusnier.bitcoinwalletbalance.ui.edit

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.jurajkusnier.bitcoinwalletbalance.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.edit_dialog_layout.view.*

@AndroidEntryPoint
class EditDialog : AppCompatDialogFragment() {

    private val viewModel: EditDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.edit_dialog_layout, null)
            .apply {
                textWalletAddress.text = getAddress()
                editTextWalletNickname.setText(getNickname())
            }

        return AlertDialog.Builder(
            requireContext(),
            R.style.ThemeOverlay_MaterialComponents_Dialog_Alert
        )
            .setTitle(getString(R.string.edit))
            .setView(view)
            .setNegativeButton(getString(R.string.close)) { _, _ -> }
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                arguments?.getString(BUNDLE_ADDRESS)?.let {
                    val newNickname = view.editTextWalletNickname.text.toString()
                    viewModel.setNickname(it, newNickname)
                }
            }.create()
    }

    private fun getAddress(): String =
        arguments?.getString(BUNDLE_ADDRESS) ?: throw Exception("Address not found")

    private fun getNickname(): String =
        arguments?.getString(BUNDLE_NICKNAME) ?: throw Exception("Nickname not found")

    companion object {
        private const val TAG = "EditDialog"
        private const val BUNDLE_ADDRESS = "address"
        private const val BUNDLE_NICKNAME = "nickname"

        fun show(fragmentManager: FragmentManager, parameters: Parameters) {
            EditDialog().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_ADDRESS, parameters.address)
                    putString(BUNDLE_NICKNAME, parameters.nickname)
                }
                show(fragmentManager, TAG)
            }
        }
    }

    data class Parameters(val address: String, val nickname: String)
}
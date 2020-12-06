package com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jurajkusnier.bitcoinwalletbalance.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.qr_dialog_layout.view.*

@AndroidEntryPoint
class QrDialog : AppCompatDialogFragment() {

    private val viewModel: QrViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.qr_dialog_layout, null)
        val walletID =
            arguments?.getString(WALLET_ID) ?: throw Exception("Unknown wallet ID in QR Dialog")

        view.textViewBitcoinAddress.text = walletID

        viewModel.barcodeBitmap.observe(this, Observer {
            view.imageViewQrCodeDetail.setImageBitmap(it)
        })

        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setBackground(ColorDrawable(Color.WHITE))
            .setPositiveButton(R.string.close) { _, _ -> }
            .create()
    }

    companion object {
        const val WALLET_ID = "WALLET_ID"
        private const val TAG = "QrDialog"

        fun show(fragmentManager: FragmentManager, address: String) {
            QrDialog().apply {
                arguments = Bundle().apply {
                    putString(WALLET_ID, address)
                }
                show(fragmentManager, TAG)
            }
        }
    }


}
package com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.qr_dialog_layout.view.*
import javax.inject.Inject

class QrDialog: DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var qrViewModel:QrViewModel


    companion object {
        private val WALLET_ID = "WALLET_ID"
        val TAG = QrDialog::class.java.simpleName

        fun newInstance(walletId: String):QrDialog {
            val dialog = QrDialog()
            val bundle = Bundle()

            bundle.putString(WALLET_ID,walletId)
            dialog.arguments = bundle

            return dialog
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        qrViewModel = ViewModelProviders.of(this, viewModelFactory).get(QrViewModel::class.java)

        val walletID = arguments?.getString(WALLET_ID) ?: throw Exception("Unknown wallet ID in QR Dialog")
        qrViewModel.generateQrCode(walletID)

        val inflater = activity?.layoutInflater
        val dialogView = inflater?.inflate(R.layout.qr_dialog_layout, null)

        dialogView?.textViewBitcoinAddress?.text = walletID

        qrViewModel.barcodeBitmap.observe(this, Observer {
            dialogView?.imageViewQrCodeDetail?.setImageBitmap(it)
        })

        return AlertDialog.Builder(context!!, R.style.AppCompatQrDialogStyle)
                .setView(dialogView)
                .setPositiveButton(R.string.close) { _, _ -> }
                .create()
    }


}
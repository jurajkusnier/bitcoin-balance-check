package com.jurajkusnier.bitcoinwalletbalance.ui.edit

import androidx.fragment.app.FragmentManager


class EditDialog {

    companion object {
        fun show(fragmentManager: FragmentManager, parameters: EditDialog.Parameters) {}
    }

    data class Parameters(val address: String, val nickname: String)
}
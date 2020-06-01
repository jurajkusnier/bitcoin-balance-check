package com.jurajkusnier.bitcoinwalletbalance.ui.edit

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class EditDialogViewModel @Inject constructor(private val editDialogRepository: EditDialogRepository) : ViewModel() {

    fun setNickname(address: String, nickname: String) {
        editDialogRepository.setNickname(address, nickname)
    }

}
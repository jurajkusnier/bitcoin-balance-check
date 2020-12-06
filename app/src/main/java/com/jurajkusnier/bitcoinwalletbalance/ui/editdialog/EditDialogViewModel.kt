package com.jurajkusnier.bitcoinwalletbalance.ui.editdialog

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EditDialogViewModel @ViewModelInject constructor(private val editDialogRepository: EditDialogRepository) :
    ViewModel() {

    fun setNickname(address: String, nickname: String) {
        viewModelScope.launch {
            editDialogRepository.setNickname(address, nickname)
        }
    }

}
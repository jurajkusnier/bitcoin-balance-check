package com.jurajkusnier.bitcoinwalletbalance.ui.addadress

import android.content.ClipDescription
import android.content.ClipboardManager
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

class AddAddressDialogViewModel @ViewModelInject constructor(private val clipboardManager: ClipboardManager) :
    ViewModel() {

    fun getClipboardText(): String? {
        if (clipboardManager.hasPrimaryClip()) {
            val clipDescription = clipboardManager.primaryClipDescription ?: return null
            if (clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) || clipDescription.hasMimeType(
                    ClipDescription.MIMETYPE_TEXT_HTML
                )
            ) {
                return clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
            }
        }

        return null
    }
}
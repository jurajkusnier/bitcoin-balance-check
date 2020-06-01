package com.jurajkusnier.bitcoinwalletbalance.ui.addadress

import android.app.Application
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel

class AddAddressDialogViewModel(private val app: Application) : AndroidViewModel(app) {

    fun getClipboardText(): String? {
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val clipDescription = clipboard.primaryClipDescription ?: return null
            if (clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) || clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
                return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            }
        }

        return null
    }
}
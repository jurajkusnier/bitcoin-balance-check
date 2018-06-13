package com.jurajkusnier.bitcoinwalletbalance.ui.addadress

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context

class AddAddressDialogViewModel(private val app:Application) : AndroidViewModel(app) {

    fun getClipboardText():String? {
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val clipDescription = clipboard.primaryClipDescription
            if (clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) || clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
                val item = clipboard.primaryClip.getItemAt(0)
                return item.text.toString()
            }
        }

        return null
    }
}
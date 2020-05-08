package com.jurajkusnier.bitcoinwalletbalance.ui.ratenowdialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.RateMePrefs
import com.jurajkusnier.bitcoinwalletbalance.utils.openPlayStore
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class RateNowDialog: DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var rateMePrefs: RateMePrefs

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        rateMePrefs.setShowed()

        return AlertDialog.Builder(requireContext(), R.style.AppCompatAlertDialogStyle)
        .setPositiveButton(getString(R.string.rate_now)) { _, _ ->
            rateMePrefs.setNeverShowAgain()
            openPlayStore()
        }
        .setNegativeButton(getString(R.string.rate_never)) { _, _ ->
            rateMePrefs.setNeverShowAgain()
        }
        .setNeutralButton(getString(R.string.rate_later)) { _, _ -> }
        .setTitle(getString(R.string.rate_title))
        .setMessage(getString(R.string.rate_text))
        .create()
    }

    companion object {
        const val TAG = "RateNowDialog"
    }
}
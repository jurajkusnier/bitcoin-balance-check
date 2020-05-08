package com.jurajkusnier.bitcoinwalletbalance.utils

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.jurajkusnier.bitcoinwalletbalance.BuildConfig

fun Fragment.openPlayStore() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")))
    } catch (_: android.content.ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")))
    }
}
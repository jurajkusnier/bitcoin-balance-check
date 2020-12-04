package com.jurajkusnier.bitcoinwalletbalance.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import kotlin.math.roundToInt

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Context.convertDpToPixel(dp:Float):Int{
    val resources = this.resources
    val metrics = resources.displayMetrics
    val px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    return px.roundToInt()
}
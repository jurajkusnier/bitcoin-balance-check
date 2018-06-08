package com.jurajkusnier.bitcoinwalletbalance.utils

fun Float.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

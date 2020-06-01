package com.jurajkusnier.bitcoinwalletbalance.utils

import java.util.*

fun Date?.isOlderThan(timeInMs: Long) = System.currentTimeMillis() - (this?.time ?: 0) > timeInMs

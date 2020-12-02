package com.jurajkusnier.bitcoinwalletbalance.data.model

data class RepositoryResponse<T>(val isLoading: Boolean, val value: T?)
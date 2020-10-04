package com.jurajkusnier.bitcoinwalletbalance.data.model

data class RepositoryResponse<T>(val source: Source, val value: T?) {
    enum class Source {
        CACHE, FINAL
    }
}
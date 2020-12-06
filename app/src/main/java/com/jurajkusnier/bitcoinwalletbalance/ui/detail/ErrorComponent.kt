package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.util.TypedValue
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.snackbar.Snackbar
import com.jurajkusnier.bitcoinwalletbalance.R

class ErrorComponent(private val view: View, lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    private var errorSnackbar: Snackbar? = null
    private val errorColor: Int by lazy {
        val value = TypedValue()
        view.context.theme.resolveAttribute(R.attr.colorError, value, true)
        value.data
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun bind(state: WalledDetailsViewState) {
        when (state.error) {
            WalledDetailsViewState.ErrorCode.UNKNOWN -> showErrorSnackbar(view.context.getString(R.string.unknown_error))
            null -> hideErrorSnackbar()
        }
    }

    private fun showErrorSnackbar(errorText: String) {
        if (errorSnackbar?.isShown == true) return
        errorSnackbar = Snackbar.make(view, errorText, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.view?.setBackgroundColor(errorColor)
        errorSnackbar?.show()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun hideErrorSnackbar() {
        errorSnackbar?.dismiss()
        errorSnackbar = null
    }
}
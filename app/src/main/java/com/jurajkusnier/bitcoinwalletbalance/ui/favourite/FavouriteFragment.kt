package com.jurajkusnier.bitcoinwalletbalance.ui.favourite

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainFragment

class FavouriteFragment : MainFragment() {

    override fun getViewModelClass(): Class<out ViewModel> = FavouriteViewModel::class.java

    override fun getEmptyText(): CharSequence = getText(R.string.favourites_list_is_empty)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (viewModel as FavouriteViewModel).unfavouritedAddress
                .observe(viewLifecycleOwner, Observer { address ->
                    showUndoSnackbar(getString(R.string.address_unfavourited)) {
                        viewModel.favouriteRecord(address)
                    }
                })
    }

    companion object {
        fun newInstance(): FavouriteFragment {
            return FavouriteFragment()
        }
    }
}
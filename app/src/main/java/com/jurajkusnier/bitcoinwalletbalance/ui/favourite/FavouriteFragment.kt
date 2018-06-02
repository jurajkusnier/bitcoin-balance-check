package com.jurajkusnier.bitcoinwalletbalance.ui.favourite

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainFragment

class FavouriteFragment: MainFragment() {

    override fun getViewModelClass(): Class<out ViewModel> = FavouriteViewModel::class.java

    override fun getEmptyText(): CharSequence = getText(R.string.favourites_list_is_empty)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.let {
            if (it !is FavouriteViewModel) return
            val thisLifecycleOwner = getViewLifecycleOwner() ?: return
            it.unfavouritedItem.observe(thisLifecycleOwner, Observer unfavouritedItemObserver@{
                unfavouritedRecord ->
                if (unfavouritedRecord == null) return@unfavouritedItemObserver
                showUndoSnackbar(unfavouritedRecord, getString(R.string.address_unfavourited))
            })
        }
    }

    companion object {
        val TAG = FavouriteFragment::class.java.simpleName

        fun newInstance(): FavouriteFragment {

            return FavouriteFragment()
        }
    }
}
package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.jurajkusnier.bitcoinwalletbalance.R
import kotlinx.android.synthetic.main.main_fragment.view.*

class ViewPagerComponent(
    view: View,
    fragment: Fragment
) {

    init {
        view.apply {
            val adapter = ViewPagerFragmentAdapter(fragment)
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.setText(R.string.favourites)
                        tab.setIcon(R.drawable.ic_favorite_24dp)
                    }
                    else -> {
                        tab.setText(R.string.history)
                        tab.setIcon(R.drawable.ic_history_24dp)
                    }
                }
            }.attach()
        }
    }

    class ViewPagerFragmentAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    FavouriteFragment()
                }
                else -> {
                    HistoryFragment()
                }
            }
        }

    }
}
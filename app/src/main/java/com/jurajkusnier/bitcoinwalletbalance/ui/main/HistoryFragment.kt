package com.jurajkusnier.bitcoinwalletbalance.ui.main;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jurajkusnier.bitcoinwalletbalance.R

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_list_fragment, container, false)
    }

    companion object {
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }
}

package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jurajkusnier.bitcoinwalletbalance.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.currency_bottomsheet_layout.view.*

@AndroidEntryPoint
class CurrencyBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: CurrencyViewModel by viewModels()
    private lateinit var currencyComponent: CurrencyComponent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.currency_bottomsheet_layout, null)
        currencyComponent = CurrencyComponent(view, viewModel::setCurrencyCode)

        viewModel.getCurrencyListItems()
            .observe(viewLifecycleOwner, Observer { list ->
                currencyComponent.bind(list)
            })

        view.bottomSheetToolbar.setNavigationOnClickListener {
            dismiss()
        }

        return view
    }

    companion object {
        private const val TAG = "CurrencyBottomSheetFragment"

        fun show(fragmentManager: FragmentManager) {
            CurrencyBottomSheetFragment().show(fragmentManager, TAG)
        }
    }
}
package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.addadress.AddAddressDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.currency.CurrencyBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_fragment.*

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        private val TAG = "MainFragment"
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels({ requireActivity() })
    private lateinit var fabComponent: FabComponent
    private lateinit var viewPagerComponent: ViewPagerComponent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        fabComponent = FabComponent(view, this::openAddAddressDialog, this::openAddAddressCamera)
        viewPagerComponent = ViewPagerComponent(view, this)
        viewModel.action.observe(viewLifecycleOwner, {
            Log.d(TAG, it.toString())
        })
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
    }

    private fun openAddAddressDialog() {
        AddAddressDialog.show(childFragmentManager)
    }

    private fun openAddAddressCamera() {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_currency) {
            CurrencyBottomSheetFragment.show(childFragmentManager)
        }
        return super.onOptionsItemSelected(item)
    }

}
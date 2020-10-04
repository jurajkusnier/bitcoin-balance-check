package com.jurajkusnier.bitcoinwalletbalance.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.currency.CurrencyBottomSheetFragment
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var fabComponent: FabComponent
    private lateinit var viewPagerComponent: ViewPagerComponent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        fabComponent = FabComponent(view, this::openAddAddressDialog, this::openAddAddressCamera)
        viewPagerComponent = ViewPagerComponent(view, this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
    }

    private fun openAddAddressDialog() {

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
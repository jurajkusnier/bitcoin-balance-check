package com.jurajkusnier.bitcoinballancecheck.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jurajkusnier.bitcoinballancecheck.R

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

        setHasOptionsMenu(true)
    }

    private fun openAddAddressDialog() {

    }

    private fun openAddAddressCamera() {

    }

}
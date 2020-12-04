package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jurajkusnier.bitcoinwalletbalance.MainActivity
import com.jurajkusnier.bitcoinwalletbalance.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_list_fragment.view.*

@AndroidEntryPoint
abstract class ListFragment : Fragment(R.layout.main_list_fragment) {

    private val viewModel: ListViewModel by viewModels()
    private lateinit var listComponent: ListComponent

    abstract fun getEmptyText(): String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupEmptyState(view)
        listComponent = ListComponent(view, object : ListAdapterActions {
            override fun onOpen(address: String) {
                openDetails(address)
            }

            override fun onEdit(address: String) {
                TODO("Not yet implemented")
            }

            override fun onToggleFavourite(address: String) {
                TODO("Not yet implemented")
            }

            override fun onDelete(address: String) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.items.observe(viewLifecycleOwner, {
            listComponent.bind(it)
        })
    }

    private fun openDetails(address: String) {
        (activity as? MainActivity)?.openDetails(address)
    }

    private fun setupEmptyState(view: View) {
        view.emptyListText.text = getEmptyText()
    }


}
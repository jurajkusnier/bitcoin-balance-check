package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jurajkusnier.bitcoinwalletbalance.MainActivity
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_list_fragment.view.*

@AndroidEntryPoint
abstract class ListFragment : Fragment(R.layout.main_list_fragment) {

    private val viewModel: ListViewModel by viewModels({ requireActivity() })
    private val actionsViewModel: ActionsViewModel by viewModels({ requireActivity() })
    private lateinit var listComponent: ListComponent

    abstract fun getEmptyText(): String

    abstract fun filter(item: WalletRecordEntity): Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupEmptyState(view)
        listComponent = ListComponent(view, object : ListAdapterActions {
            override fun onOpen(address: String) {
                openDetails(address)
            }

            override fun onEdit(item: WalletRecordEntity) {
                actionsViewModel.showEditDialog(item)
            }

            override fun onToggleFavourite(item: WalletRecordEntity) {
                actionsViewModel.toggleFavourite(item)
            }

            override fun onDelete(address: String) {
                actionsViewModel.deleteItem(address)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.items.observe(viewLifecycleOwner, {
            listComponent.bind(it.filter { item -> filter(item) })
        })
    }

    private fun openDetails(address: String) {
        (activity as? MainActivity)?.openDetails(address)
    }

    private fun setupEmptyState(view: View) {
        view.emptyListText.text = getEmptyText()
    }


}
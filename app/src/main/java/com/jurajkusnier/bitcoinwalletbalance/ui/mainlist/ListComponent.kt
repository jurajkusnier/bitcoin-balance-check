package com.jurajkusnier.bitcoinwalletbalance.ui.mainlist

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import com.jurajkusnier.bitcoinwalletbalance.ui.main.ListAdapterActions
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainListAdapter
import kotlinx.android.synthetic.main.main_list_fragment.view.*

class ListComponent(private val view: View, callback: ListAdapterActions) {

    private val listAdapter = MainListAdapter(callback) { position ->
        view.recyclerViewMain.smoothScrollToPosition(position)
    }

    init {
        setupRecyclerView(view)
    }

    fun bind(items: List<WalletRecordEntity>) {
        if (items.isEmpty()) {
            view.layoutEmptyList.visibility = View.VISIBLE
        } else {
            view.layoutEmptyList.visibility = View.GONE
        }
        listAdapter.submitList(items)
    }

    private fun setupRecyclerView(view: View) {
        view.recyclerViewMain.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = listAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }


}
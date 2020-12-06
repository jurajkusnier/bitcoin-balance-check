package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import com.jurajkusnier.bitcoinwalletbalance.utils.inflate
import com.jurajkusnier.bitcoinwalletbalance.utils.sathoshiToBTCstring
import kotlinx.android.synthetic.main.main_recycler_item.view.*

class MainListAdapter(
    private val callback: ListAdapterActions,
    private val scrollTo: (Int) -> Unit
) :
    ListAdapter<WalletRecordEntity, MainListAdapter.ViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.main_recycler_item, false)
        return ViewHolder(view, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCurrentListChanged(
        previousList: MutableList<WalletRecordEntity>,
        currentList: MutableList<WalletRecordEntity>
    ) {
        if (previousList.size + 1 == currentList.size) {
            previousList.forEachIndexed { index, item ->
                if (currentList[index].address != item.address) {
                    scrollTo(index)
                    return
                }
            }
        }
    }

    class ViewHolder(
        private val view: View,
        private val callback: ListAdapterActions
    ) : RecyclerView.ViewHolder(view) {

        fun bind(data: WalletRecordEntity) {

            view.imageViewItemMenu.setOnClickListener clickListener@{
                showMenu(data)
            }

            view.itemCardView.setOnClickListener {
                callback.onOpen(data.address)
            }

            view.primaryTextView.text = if (data.nickname.isNotBlank()) {
                data.nickname
            } else {
                data.address
            }
            view.secondaryTextView.text = sathoshiToBTCstring(data.finalBalance)

            if (data.favourite) {
                view.imageDefaultLogo.visibility = View.INVISIBLE
                view.imageFavouritedLogo.visibility = View.VISIBLE
            } else {
                view.imageDefaultLogo.visibility = View.VISIBLE
                view.imageFavouritedLogo.visibility = View.INVISIBLE
            }
        }

        private fun showMenu(item: WalletRecordEntity) {
            PopupMenu(view.context, view.imageViewItemMenu).apply {
                inflate(R.menu.popup_menu)
                if (item.favourite) {
                    menu.findItem(R.id.menu_favourite).isVisible = false
                    menu.findItem(R.id.menu_unfavourite).isVisible = true
                } else {
                    menu.findItem(R.id.menu_favourite).isVisible = true
                    menu.findItem(R.id.menu_unfavourite).isVisible = false
                }
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            callback.onEdit(item)
                        }
                        R.id.menu_unfavourite, R.id.menu_favourite -> {
                            callback.onToggleFavourite(item)
                        }
                        R.id.menu_delete -> {
                            callback.onDelete(item.address)
                        }
                    }
                    true
                }
                show()
            }
        }
    }

    companion object {
        val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<WalletRecordEntity>() {
            override fun areItemsTheSame(
                oldItem: WalletRecordEntity,
                newItem: WalletRecordEntity
            ): Boolean {
                return oldItem.address == newItem.address
            }

            override fun areContentsTheSame(
                oldItem: WalletRecordEntity,
                newItem: WalletRecordEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
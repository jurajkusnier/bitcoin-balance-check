package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.content.Context
import android.support.transition.Fade
import android.support.transition.Slide
import android.support.transition.TransitionSet
import android.support.v7.app.AppCompatActivity
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuPopupHelper
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.ui.detail.DetailFragment
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialogInterface
import com.jurajkusnier.bitcoinwalletbalance.ui.favourite.FavouriteViewModel
import com.jurajkusnier.bitcoinwalletbalance.utils.CustomDate
import com.jurajkusnier.bitcoinwalletbalance.utils.inflate
import com.jurajkusnier.bitcoinwalletbalance.utils.sathoshiToBTCstring
import kotlinx.android.synthetic.main.main_recycler_item.view.*


class TaskDiffCallback : DiffUtil.ItemCallback<WalletRecord>() {
    override fun areItemsTheSame(oldItem: WalletRecord?, newItem: WalletRecord?): Boolean {
        return oldItem?.address == newItem?.address
    }

    override fun areContentsTheSame(oldItem: WalletRecord?, newItem: WalletRecord?): Boolean {
        return oldItem == newItem
    }
}


class MainListAdapter(context:Context, private val mainViewModel: MainViewModel, private val editDialogInterface: EditDialogInterface) :
        ListAdapter<WalletRecord, MainListAdapter.ViewHolder>(TaskDiffCallback()) {

    val TAG = MainListAdapter::class.java.name
    val customDate = CustomDate(context)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.main_recycler_item,false)

        val parentContext = parent.context
        val viewHolder = ViewHolder(inflatedView)

        inflatedView.linearLayoutOneItem.setOnClickListener clickListener@{
            if (parentContext is AppCompatActivity) {

                val position = viewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) return@clickListener

                val transactionTime = 300L

                val previousFragment = parentContext.supportFragmentManager.findFragmentById(R.id.container)
                val nextFragment = DetailFragment.newInstance(getItem(position).address)

                val exitFade = Fade()
                exitFade.duration = transactionTime
                previousFragment.exitTransition = exitFade
                nextFragment.enterTransition = TransitionSet()
                        .addTransition(Fade(Fade.IN)
                            .setInterpolator { (it - 0.5f) * 2 })
                        .addTransition(Slide()).setDuration(transactionTime)

                parentContext.supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(DetailFragment.TAG)
                        .replace(R.id.container, nextFragment )
                        .commit()
            }
        }

        inflatedView.imageViewItemMenu.setOnClickListener clickListener@{

            val menu = PopupMenu(parent.context, it)
            menu.setOnMenuItemClickListener { it ->
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    when (it.itemId) {
                        R.id.menu_edit -> {
                            val item = getItem(position)
                            editDialogInterface.showEditDialog(item.address,item.nickname)
                            true
                        }
                        R.id.menu_unfavourite -> {
                            mainViewModel.unfavouriteRecord(getItem(position))
                            true
                        }
                        R.id.menu_favourite -> {
                            mainViewModel.favouriteRecord(getItem(position))
                            true
                        }
                        R.id.menu_delete -> {
                            mainViewModel.deleteRecord(getItem(position))
                            true
                        }
                        else -> {
                            false
                        }
                    }
                else
                    false
            }
            menu.inflate(R.menu.popup_menu)
            if (getItem(viewHolder.adapterPosition).favourite) {
                menu.menu.findItem(R.id.menu_favourite).isVisible = false
                menu.menu.findItem(R.id.menu_unfavourite).isVisible = true
            } else {
                menu.menu.findItem(R.id.menu_favourite).isVisible = true
                menu.menu.findItem(R.id.menu_unfavourite).isVisible = false
            }

            if (mainViewModel is FavouriteViewModel) {
                menu.menu.findItem(R.id.menu_delete).isVisible = false
            }

            val menuHelper = MenuPopupHelper(parent.context, menu.menu as MenuBuilder, it)
            menuHelper.setForceShowIcon(true)
            menuHelper.show()
        }

        return viewHolder
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {

        fun bind(data: WalletRecord) {

            v.visibility=View.VISIBLE
            v.textViewItemWalletId.text = data.address
            v.textViewItemBalance.text = sathoshiToBTCstring(data.finalBalance)
            v.textViewItemDate.text = customDate.getLastUpdatedString(data.lastAccess)

            if (data.favourite) {
                v.imageDefaultLogo.visibility = View.INVISIBLE
                v.imageFavouritedLogo.visibility = View.VISIBLE
            } else {
                v.imageDefaultLogo.visibility = View.VISIBLE
                v.imageFavouritedLogo.visibility = View.INVISIBLE
            }

            if (data.nickname.isNotBlank()) {
                v.textViewItemWalletNickname.text = data.nickname
                v.textViewItemWalletNickname.visibility = View.VISIBLE
            } else {
                v.textViewItemWalletNickname.visibility = View.GONE
            }

        }
    }

}
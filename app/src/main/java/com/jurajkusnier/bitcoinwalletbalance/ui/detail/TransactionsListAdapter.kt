package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.model.OneTransaction
import com.jurajkusnier.bitcoinwalletbalance.utils.CustomDate
import com.jurajkusnier.bitcoinwalletbalance.utils.inflate
import com.jurajkusnier.bitcoinwalletbalance.utils.sathoshiToBTCstring
import kotlinx.android.synthetic.main.transaction_layout.view.*

class TransactionListAdapter(private val myWalletID:String, private val transactions:Array<OneTransaction>, private val context:Context):
        RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {

    private lateinit var recyclerView: ViewGroup
    private var mExpandedPosition = RecyclerView.NO_POSITION

    val colorMinus = ContextCompat.getColor(context, R.color.colorRed)
    val colorPlus = ContextCompat.getColor(context, R.color.colorPrimary)
    val transactionPlus = context.getString(R.string.transaction_plus)
    val transactionMinus = context.getString(R.string.transaction_minus)
    val customDate = CustomDate(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        recyclerView = parent
        val infltedView = parent.inflate(R.layout.transaction_layout,false)
        val viewHolder = ViewHolder(myWalletID, infltedView)

        viewHolder.itemView.setOnClickListener clickListener@ {
            val position = viewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) return@clickListener

            // collapse any currently expanded items
            if (RecyclerView.NO_POSITION != mExpandedPosition) {
                notifyItemChanged(mExpandedPosition, COLLAPSE)
            }

            if (mExpandedPosition != position) {
                mExpandedPosition = position
                notifyItemChanged(position, EXPAND)
            } else {
                mExpandedPosition = RecyclerView.NO_POSITION
            }
        }

        return viewHolder
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(EXPAND) || payloads.contains(COLLAPSE)) {
            holder.setExpanded(position == mExpandedPosition)
        } else {
            onBindViewHolder(holder,position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactions[position],position == mExpandedPosition)
    }


    inner class ViewHolder(private val myWalletID: String, private val v: View) : RecyclerView.ViewHolder(v) {

        fun bind(data: OneTransaction, isExpanded:Boolean) {
            var sumSent = 0L
            var sumGet = 0L

            for (item in data.inputs) {
                if (myWalletID == item.prev_out?.addr) {
                    sumSent += item.prev_out.value
                }
            }

            for (item in data.out) {
                if (myWalletID == item.addr) {
                    sumGet+= item.value
                }
            }

            if (sumGet > sumSent) {
                v.textViewTransactionCrypto.setTextColor(colorPlus)
                v.textViewTransactionCrypto.text = String.format(transactionPlus,sathoshiToBTCstring(Math.abs(sumGet - sumSent)))
            }
            else {
                v.textViewTransactionCrypto.setTextColor(colorMinus)
                v.textViewTransactionCrypto.text = String.format(transactionMinus,sathoshiToBTCstring(Math.abs(sumGet - sumSent)))
            }

            v.textViewTransactionDate.text = customDate.getDate(data.time)
            v.textViewTransactionID.text = data.hash

            setExpanded(isExpanded)
        }

        fun setExpanded(isExpanded: Boolean) {
            v.isActivated = isExpanded

            if (isExpanded) {
                v.textViewTransactionID.visibility = View.VISIBLE
                v.textViewTransactionIDLabel.visibility = View.VISIBLE
            } else {
                v.textViewTransactionID.visibility = View.GONE
                v.textViewTransactionIDLabel.visibility = View.GONE
            }
        }
    }

    private val EXPAND = 0x1
    private val COLLAPSE = 0x2
}
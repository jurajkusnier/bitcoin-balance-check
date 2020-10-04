package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import kotlinx.android.synthetic.main.currency_list_footer.view.*
import kotlinx.android.synthetic.main.currency_list_item.view.*

class CurrencyItemDiffCallback : DiffUtil.ItemCallback<CurrencyItem>() {

    override fun areItemsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
        if (oldItem::class != newItem::class) return false
        return when (oldItem) {
            is CurrencyItem.Currency -> areCurrencyItemsTheSame(oldItem, newItem as CurrencyItem.Currency)
            CurrencyItem.Loading -> true
            is CurrencyItem.LastUpdate -> true
        }
    }

    override fun areContentsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
        if (oldItem::class != newItem::class) return false
        return when (oldItem) {
            is CurrencyItem.Currency -> oldItem == newItem as CurrencyItem.Currency
            CurrencyItem.Loading -> true
            is CurrencyItem.LastUpdate -> oldItem == newItem as CurrencyItem.LastUpdate
        }
    }

    private fun areCurrencyItemsTheSame(oldItem: CurrencyItem.Currency, newItem: CurrencyItem.Currency): Boolean {
        return oldItem.exchangeRateWithCurrencyCode.currencyCode == newItem.exchangeRateWithCurrencyCode.currencyCode
    }
}

class CurrencyAdapter(private val itemSelected: (CurrencyCode) -> Unit) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    private val diffCallback = CurrencyItemDiffCallback()
    private val differ: AsyncListDiffer<CurrencyItem> = AsyncListDiffer(this, diffCallback)

    fun setListItems(listItems: List<CurrencyItem>) {
        differ.submitList(listItems)
    }

    sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class LoadingViewHolder(view: View) : ViewHolder(view)

        class ItemViewHolder(private val view: View, private val itemSelected: (CurrencyCode) -> Unit) : ViewHolder(view) {
            fun bind(item: CurrencyItem.Currency) {
                view.radioButton.text = item.exchangeRateWithCurrencyCode.currencyCode
                val exchangeRate = item.exchangeRateWithCurrencyCode.exchangeRate
                if (exchangeRate != null) {
                    view.radioText.text = view.resources.getString(R.string.BTC_to_currency, exchangeRate.rate, item.exchangeRateWithCurrencyCode.currencyCode)
                }
                view.radioButton.isChecked = item.isSelected
                view.radioButton.setOnClickListener {
                    itemSelected(item.exchangeRateWithCurrencyCode.currencyCode)
                }
            }
        }

        class FooterViewHolder(private val view: View) : ViewHolder(view) {
            private val customDate = "TODO"// CustomDate(view.context)

            fun bind(item: CurrencyItem.LastUpdate) {
                view.textViewLastUpdated.text = if (item.date.time == 0L) {
                    ""
                } else {
                    customDate//.getLastUpdatedString(item.date.time)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING_SKELETON -> createLoadingViewHolder(inflater, parent)
            VIEW_TYPE_ITEM -> createItemViewHolder(inflater, parent)
            VIEW_TYPE_FOOTER -> createFooterViewHolder(inflater, parent)
            else -> throw Exception("Unknown viewType [$viewType]")
        }
    }

    private fun createLoadingViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder.LoadingViewHolder {
        val view = inflater.inflate(R.layout.currency_list_skeleton_item, parent, false)
        return ViewHolder.LoadingViewHolder(view)
    }

    private fun createItemViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder.ItemViewHolder {
        val view = inflater.inflate(R.layout.currency_list_item, parent, false)
        return ViewHolder.ItemViewHolder(view, itemSelected)
    }

    private fun createFooterViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder.FooterViewHolder {
        val view = inflater.inflate(R.layout.currency_list_footer, parent, false)
        return ViewHolder.FooterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder.ItemViewHolder -> holder.bind(differ.currentList[position] as CurrencyItem.Currency)
            is ViewHolder.FooterViewHolder -> holder.bind(differ.currentList[position] as CurrencyItem.LastUpdate)
            is ViewHolder.LoadingViewHolder -> {
            }
        }
    }

    override fun getItemViewType(position: Int) = when (differ.currentList[position]) {
        is CurrencyItem.Loading -> VIEW_TYPE_LOADING_SKELETON
        is CurrencyItem.Currency -> VIEW_TYPE_ITEM
        is CurrencyItem.LastUpdate -> VIEW_TYPE_FOOTER
    }

    override fun getItemCount(): Int = differ.currentList.size

    companion object {
        const val VIEW_TYPE_LOADING_SKELETON = 0
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_FOOTER = 2
    }

}
package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialogInterface
import com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog.QrDialog
import com.jurajkusnier.bitcoinwalletbalance.utils.convertDpToPixel
import com.jurajkusnier.bitcoinwalletbalance.utils.sathoshiToBTCstring
import com.squareup.moshi.Moshi
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.detail_fragment.*
import javax.inject.Inject
import kotlin.math.abs

class DetailFragment : DaggerFragment(), AppBarLayout.OnOffsetChangedListener, EditDialogInterface {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var moshi: Moshi
    private var optionsMenu: Menu? = null
    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)

        val walletID = getAddress() ?: return //TODO: maybe throw exception
        setupUI(walletID)

        viewModel.loadWalletDetails(walletID)

        viewModel.walledDetailsViewState.observe(viewLifecycleOwner, Observer {
            swiperefresh.isRefreshing = it.loading
            if (it.error != null && it.wallet != null) {
                textInfo.visibility = View.VISIBLE
            } else {
                textInfo.visibility = View.GONE
            }
            when (it.error) {
                WalledDetailsViewState.ErrorCode.UNKNOWN -> showErrorSnackbar(getString(R.string.network_connection_error))
                WalledDetailsViewState.ErrorCode.OFFLINE -> showErrorSnackbar(getString(R.string.offline_error))
                WalledDetailsViewState.ErrorCode.INVALID_ADDRESS -> showErrorSnackbar(getString(R.string.invalid_bitcoin_address))
                null -> hideErrorSnackbar()
            }
            updateOptionsMenu(it.loading, it.wallet?.favourite == true, it.wallet != null)
            val btcString = sathoshiToBTCstring(it?.wallet?.finalBalance ?: 0)
            textFinalBalanceCrypto.text = btcString
            newTitle.text = btcString
            textTotalReceived.text = sathoshiToBTCstring(it?.wallet?.totalReceived ?: 0)
            textTotalSent.text = sathoshiToBTCstring(it?.wallet?.totalSent ?: 0)
            updateTransactionListView(it)

            if ((it.wallet == null || it.wallet.transactions.isEmpty()) && !it.loading) {
                textViewNoTransaction.visibility = View.VISIBLE
            } else {
                textViewNoTransaction.visibility = View.GONE
            }

            textFinalBalanceMoney.text = if (it?.wallet?.finalBalance != null && it.exchangeRate?.rate != null) {
                val btcValue = it.wallet.finalBalance / 100_000_000.00
                val fiat = btcValue * it.exchangeRate.rate
                getString(R.string.btc_price_in_fiat, fiat, it.currencyCode)
            } else ""
        })

        viewModel.showEditDialog.observe(viewLifecycleOwner, Observer {
            EditDialog.newInstance(it).show(fragmentManager!!, EditDialog.TAG)
        })

        buttonCopyAddressToClipboard.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getText(R.string.address), textWalletID.text)
            clipboard.primaryClip = clip
            Toast.makeText(context, getString(R.string.address_copied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAddress(): String? {
        return arguments?.getString(WALLET_ID)
    }

    private fun updateOptionsMenu(isLoading: Boolean, isFavorited: Boolean, canEdit: Boolean) {
        optionsMenu?.findItem(R.id.menu_refresh)?.isEnabled = !isLoading
        optionsMenu?.findItem(R.id.menu_favourite)?.isVisible = !isFavorited
        optionsMenu?.findItem(R.id.menu_unfavourite)?.isVisible = isFavorited
        optionsMenu?.findItem(R.id.menu_edit)?.isVisible = canEdit
    }

    private var errorSnackbar: Snackbar? = null
    private val colorAccent: Int by lazy {
        ResourcesCompat.getColor(resources, R.color.colorAccent, null)
    }

    private fun showErrorSnackbar(errorText: String) {
        if (errorSnackbar?.isShown == true) return
        errorSnackbar = Snackbar.make(detailLayout, errorText, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.view?.setBackgroundColor(colorAccent)
        errorSnackbar?.show()
    }

    private fun hideErrorSnackbar() {
        errorSnackbar?.dismiss()
        errorSnackbar = null
    }

    private fun setupUI(address: String) {
        appbar.addOnOffsetChangedListener(this)
        textWalletID.text = address

        imageViewQrCode.setOnClickListener {
            QrDialog.newInstance(address).show(requireActivity().supportFragmentManager, QrDialog.TAG)
        }

        setupToolbar()
        setupRecyclerView()
        setupSwipeToRefresh(address)
        setSquareDetailCardView()
    }

    private fun setSquareDetailCardView() {
        detailCardView.minimumHeight = resources.displayMetrics.heightPixels
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (appBarLayout == null) return
        val range = appBarLayout.totalScrollRange.toFloat()

        when {
            abs(verticalOffset) == range.toInt() -> {
                // Collapsed
                newTitle.alpha = 1.0f
                swiperefresh.isEnabled = false
            }
            verticalOffset == 0 -> {
                // Expanded
                newTitle.alpha = 0.0f
                swiperefresh.isEnabled = true
            }
            else -> {
                // Somewhere in between
                swiperefresh.isEnabled = false
                val value = abs(verticalOffset / range) - 0.5f
                newTitle.alpha = value * 2f
            }
        }
    }

    private fun updateTransactionListView(walletDetailsViewState: WalledDetailsViewState) {
        walletDetailsViewState.wallet?.let {
            recyclerViewTransactions?.adapter = TransactionListAdapter(it.address, it.transactions, activity as Context)
            recyclerViewTransactions?.adapter?.notifyDataSetChanged()
            recyclerViewTransactions?.isNestedScrollingEnabled = false
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbarResults)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = ""
        }
    }

    private fun setupRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
            adapter = null
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun setupSwipeToRefresh(address: String) {
        //TODO: load position automatically from layout (imgDetailsBitcoin)
        swiperefresh.setProgressViewOffset(false, 0, requireContext().convertDpToPixel(58.5f))
        swiperefresh.setOnRefreshListener {
            viewModel.loadWalletDetails(address, true)
        }
    }

    override fun showEditDialog(address: String, nickname: String) {
        EditDialog.newInstance(EditDialog.Parameters(address, nickname)).show(fragmentManager!!, EditDialog.TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
        optionsMenu = menu
    }

    override fun onDestroy() {
        super.onDestroy()
        errorSnackbar?.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                getAddress()?.let { viewModel.loadWalletDetails(it, true) }
                true
            }
            R.id.menu_favourite -> {
                viewModel.favouriteRecord()
                Toast.makeText(context, getString(R.string.address_favourited), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_unfavourite -> {
                viewModel.unfavouriteRecord()
                Toast.makeText(context, getString(R.string.address_unfavourited), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_edit -> {
                viewModel.showEditDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "DetailFragment"
        const val WALLET_ID = "WALLET_ID"

        fun newInstance(walletID: String): DetailFragment {
            val detailFragment = DetailFragment()
            val bundle = Bundle()
            bundle.putString(WALLET_ID, walletID)
            detailFragment.arguments = bundle
            return detailFragment
        }
    }

}
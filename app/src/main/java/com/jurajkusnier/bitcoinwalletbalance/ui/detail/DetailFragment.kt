package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.transition.Transition
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordView
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRate
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialogInterface
import com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog.QrDialog
import com.jurajkusnier.bitcoinwalletbalance.utils.convertDpToPixel
import com.jurajkusnier.bitcoinwalletbalance.utils.format
import com.jurajkusnier.bitcoinwalletbalance.utils.sathoshiToBTCstring
import com.squareup.moshi.Moshi
import dagger.android.AndroidInjection
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.detail_fragment.*
import kotlinx.android.synthetic.main.detail_fragment.view.*
import javax.inject.Inject


class DetailFragment: DaggerFragment(), AppBarLayout.OnOffsetChangedListener, EditDialogInterface {

    var isAnimating = false

    override fun setEnterTransition(transition: Any?) {

        if (transition is android.support.transition.Transition) {
            transition.addListener(object :Transition.TransitionListener {
                override fun onTransitionResume(transition: Transition) {

                }

                override fun onTransitionPause(transition: Transition) {

                }

                override fun onTransitionCancel(transition: Transition) {

                }

                override fun onTransitionStart(transition: Transition) {

                    isAnimating = true
                }

                override fun onTransitionEnd(transition: Transition) {
                    isAnimating = false
                    updateTransactionListView()
                }

            })
        }

        super.setEnterTransition(transition)
    }

    private fun updateTransactionListView() {
        _walletRecord?.let {
            recyclerViewTransactions?.adapter = TransactionListAdapter(it.address, it.transactions, activity as Context)
            recyclerViewTransactions?.adapter?.notifyDataSetChanged()
            recyclerViewTransactions?.isNestedScrollingEnabled = false
        }
    }

    override fun showEditDialog(address: String, nickname: String) {
        EditDialog.newInstance(address,nickname).show(fragmentManager, EditDialog.TAG)
    }

    private lateinit var viewModel:DetailViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var moshi: Moshi

    companion object {
        val TAG = DetailFragment::class.java.simpleName
        val WALLET_ID = "WALLET_ID"

        fun newInstance(walletID:String):DetailFragment {
            val detailFragment =DetailFragment()
            val bundle = Bundle()
            bundle.putString(WALLET_ID, walletID)
            detailFragment.arguments = bundle

            return detailFragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        errorSnackbar?.dismiss()
    }

    override fun onAttach(context: Context?) {
        //DI activity injection first
        AndroidInjection.inject(activity)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.detail_fragment, container, false)

        //Let's make detail view look nicer on every screen
        context?.let {
            val displayMetrics = it.resources.displayMetrics
            view.detailCardView.minimumHeight = displayMetrics.heightPixels
        }

        //TODO: load position automatically from layout (imgDetailsBitcoin)
        context?.let {
            view.swiperefresh.setProgressViewOffset(false, 0, it.convertDpToPixel(58.5f))
        }

        return view
    }

    var _walletRecord:WalletRecordView? = null
    var _exchangeRate:ExchangeRate? = null

    private fun getBitcoinPriceInMoney():String {
        _exchangeRate?.let { rate ->
            _walletRecord?.let {
                return "${(rate.price * it.finalBalance / 100_000_000).format(2)} ${rate.currency}"
            }
        }

        return ""
    }

    var stillLoading = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        //ViewModel
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)

        val walletID = arguments?.getString(WALLET_ID)
        if (walletID != null) {
            viewModel.initViewModel(walletID)

            imageViewQrCode.setOnClickListener {
                QrDialog.newInstance(walletID).show(activity?.supportFragmentManager,QrDialog.TAG)
            }
        }

//        viewModel.initViewModel("14FWSLwNWHCQjsA2teKSKeiaxA3F5kH1tZ") // Empty wallet
//        viewModel.initViewModel("14s299LGRmSX5dxtcuY4gqUgn2tW3nCz8m") // Not empty wallet
//        viewModel.initViewModel("1r8JvHjYFiFDNdrDBW1iqDp8pmoaWuSaz") // Lot of transactions



        textWalletID.text = walletID

        //UI initialization
        val thisContext = activity
        if (thisContext != null) {
            colorAccent = ContextCompat.getColor(thisContext, R.color.colorAccent)
            initView(thisContext)
        }

        viewModel.liveExchangeRate.observe(this, Observer {

            _exchangeRate = it
            textFinalBalanceMoney.text = getBitcoinPriceInMoney()

        })

        viewModel.walletDetail.observe(this, Observer walletRecordObserver@{

            _walletRecord = it

            refreshOptionsMenu()

            textFinalBalanceCrypto.text = sathoshiToBTCstring(it?.finalBalance ?: 0)
            textTotalReceived.text = sathoshiToBTCstring(it?.totalReceived ?: 0)
            textTotalSent.text = sathoshiToBTCstring(it?.totalSent ?: 0)

            if (thisContext != null && it != null && !isAnimating) {
                    updateTransactionListView()
            }

            if ((it == null || it.transactions.isEmpty()) && !stillLoading) {
                textViewNoTransaction.visibility = View.VISIBLE
            } else {
                textViewNoTransaction.visibility = View.GONE
            }

            textFinalBalanceMoney.text = getBitcoinPriceInMoney()
        })

        viewModel.loadingState.observe(this, Observer<DetailViewModel.LoadingState> {
            stillLoading = (it == DetailViewModel.LoadingState.LOADING)

            swiperefresh.isRefreshing = stillLoading
            refreshOptionsMenu()

            when (it) {
                DetailViewModel.LoadingState.ERROR -> showErrorSnackbar(getString(R.string.network_connection_error))
                DetailViewModel.LoadingState.ERROR_OFFLINE -> showErrorSnackbar(getString(R.string.offline_error))
                DetailViewModel.LoadingState.ERROR_INVALID_ADDRESS -> showErrorSnackbar(getString(R.string.invalid_bitcoin_address))
                else -> hideErrorShackbar()
            }
        })

        buttonCopyAddressToClipboard.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getText(R.string.address),textWalletID.text)
            clipboard.primaryClip = clip
            Toast.makeText(context,getString(R.string.address_copied),Toast.LENGTH_SHORT).show()
        }


    }

    fun refreshOptionsMenu() {
        //Loading
        optionsMenu?.findItem(R.id.menu_refresh)?.isEnabled = !stillLoading
        //Others
        optionsMenu?.findItem(R.id.menu_favourite)?.isVisible = _walletRecord?.favourite == false
        optionsMenu?.findItem(R.id.menu_unfavourite)?.isVisible = _walletRecord?.favourite == true
        optionsMenu?.findItem(R.id.menu_edit)?.isVisible = (_walletRecord != null)

    }

    private var errorSnackbar: Snackbar? = null
    private var colorAccent:Int = Color.RED

    private fun showErrorSnackbar(errorText:String) {
        errorSnackbar = Snackbar.make(detailLayout, errorText, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.view?.setBackgroundColor(colorAccent)
        errorSnackbar?.show()
    }

    private fun hideErrorShackbar() {
        errorSnackbar?.dismiss()
    }

    private fun initView(context: Context) {

        if (context is AppCompatActivity) {
            context.setSupportActionBar(toolbarResults)
            context.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            context.supportActionBar?.setDisplayShowHomeEnabled(true)
            context.supportActionBar?.title=""
        }

        appbar.addOnOffsetChangedListener(this)

        recyclerViewTransactions.layoutManager = LinearLayoutManager(context)
        recyclerViewTransactions.setHasFixedSize(false)
        recyclerViewTransactions.adapter = null

        textInfo.visibility = View.GONE

        val mDividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerViewTransactions.addItemDecoration(mDividerItemDecoration)

        swiperefresh.setOnRefreshListener {
            viewModel.loadWalletDetails(true)
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (appBarLayout == null) return

        val range= appBarLayout.totalScrollRange.toFloat()

        when {
            Math.abs(verticalOffset) == range.toInt() -> {
                // Collapsed
                newTitle.alpha=1.0f
                swiperefresh.isEnabled = false
            }
            verticalOffset == 0 -> {
                // Expanded
                newTitle.alpha=0.0f
                swiperefresh.isEnabled = true
            }
            else -> {
                // Somewhere in between
                swiperefresh.isEnabled = false
                val value = Math.abs(verticalOffset/range) - 0.5f
                newTitle.alpha = value * 2f
            }
        }
    }

    var optionsMenu: Menu? = null

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        optionsMenu = menu
        inflater?.inflate(R.menu.detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        refreshOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.menu_refresh -> {
                viewModel.loadWalletDetails(true)
                true
            }
            R.id.menu_favourite -> {
                _walletRecord?.let {
                    viewModel.favouriteRecord()
                }
                true
            }
            R.id.menu_unfavourite-> {
                _walletRecord?.let {
                    viewModel.unfavouriteRecord()
                }
                true
            }
            R.id.menu_edit-> {
                _walletRecord?.let {
                    showEditDialog(it.address,it.nickname)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

}
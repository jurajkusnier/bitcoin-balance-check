package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.model.RawData
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import com.jurajkusnier.bitcoinwalletbalance.utils.sathoshiToBTCstring
import dagger.android.AndroidInjection
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.detail_fragment.*
import javax.inject.Inject


class DetailFragment: DaggerFragment(), AppBarLayout.OnOffsetChangedListener {

    private lateinit var viewModel:DetailViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory

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
        return inflater.inflate(R.layout.detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //ViewModel
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)

        val walletID = arguments?.getString(WALLET_ID)
        if (walletID != null) {
            viewModel.initViewModel(walletID)
        }

//        viewModel.initViewModel("14FWSLwNWHCQjsA2teKSKeiaxA3F5kH1tZ") // Empty wallet
//        viewModel.initViewModel("14s299LGRmSX5dxtcuY4gqUgn2tW3nCz8m") // Not empty wallet


        textWalletID.text = walletID
        //UI initialization
        val thisContext = activity
        if (thisContext != null) {
            colorAccent = ContextCompat.getColor(thisContext, R.color.colorAccent)
            initView(thisContext)
        }

        viewModel.rawData.observe (this, Observer<RawData> RawDataObserver@{
            rawData ->
            Log.d(TAG,rawData.toString())

            if (rawData == null) return@RawDataObserver

            val balanceInSatoshi = rawData.final_balance

            textViewNoTransaction.visibility = if (rawData.txs.isNotEmpty()) View.GONE else View.VISIBLE

            newTitle.text = textFinalBalanceCrypto.text

            textFinalBalanceCrypto.text = sathoshiToBTCstring(balanceInSatoshi)
            textTotalReceived.text = sathoshiToBTCstring(rawData.total_received)
            textTotalSent.text = sathoshiToBTCstring(rawData.total_sent)

            if (thisContext != null) {
                recyclerViewTransactions.adapter = TransactionListAdapter(rawData.address, rawData.txs, thisContext)
                recyclerViewTransactions.adapter.notifyDataSetChanged()
                recyclerViewTransactions.isNestedScrollingEnabled = false
            }
        })

        viewModel.loadingState.observe(this, Observer<DetailViewModel.LoadingState> {
            swiperefresh.isRefreshing = (it == DetailViewModel.LoadingState.LOADING)
            if (it == DetailViewModel.LoadingState.ERROR) {
                showErrorSnackbar()
            } else {
                hideErrorShackbar()
            }
        })
    }

    private var errorSnackbar: Snackbar? = null
    private var colorAccent:Int = Color.RED

    private fun showErrorSnackbar() {
        errorSnackbar = Snackbar.make(detailLayout,getString(R.string.network_connection_error),Snackbar.LENGTH_INDEFINITE)
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
            viewModel.loadWalletDetails()
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
}
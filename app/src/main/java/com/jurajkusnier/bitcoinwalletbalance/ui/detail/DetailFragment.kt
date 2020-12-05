package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.main.ActionsViewModel
import com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog.QrDialog
import com.jurajkusnier.bitcoinwalletbalance.utils.convertDpToPixel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.detail_fragment.*
import kotlinx.android.synthetic.main.detail_fragment.view.*
import kotlin.math.abs

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.detail_fragment), AppBarLayout.OnOffsetChangedListener {

    private val viewModel: DetailViewModel by viewModels()
    private val actionsViewModel: ActionsViewModel by viewModels({ requireActivity() })
    private lateinit var detailInfoComponent: DetailInfoComponent
    private lateinit var errorComponent: ErrorComponent
    private lateinit var transactionsComponent: TransactionsComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        detailInfoComponent = DetailInfoComponent(view, getAddress())
        errorComponent = ErrorComponent(view, viewLifecycleOwner)
        transactionsComponent =
            TransactionsComponent(view, TransactionListAdapter(getAddress(), requireContext()))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.walledDetailsViewState.observe(viewLifecycleOwner, {
            activity?.invalidateOptionsMenu()
            swiperefresh.isRefreshing = it.loading

            detailInfoComponent.bind(it)
            errorComponent.bind(it)
            transactionsComponent.bind(it)
        })

        viewModel.action.observe(viewLifecycleOwner, {
            if (it is Actions.ShowEditDialog) {
                EditDialog.show(childFragmentManager, it.parameters)
            }
        })
    }

    private fun getAddress(): String {
        return arguments?.getString(WALLET_ID) ?: throw Exception("Wallet id not found")
    }

    private fun setupView(view: View) {
        setupQrCode(view)
        setupToolbar(view)
        setupRecyclerView(view)
        setupSwipeToRefresh(view)
        setupClipboardButton(view)
    }

    private fun setupClipboardButton(view: View) {
        view.buttonCopyAddressToClipboard.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getText(R.string.address), view.textWalletID.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, getString(R.string.address_copied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupQrCode(view: View) {
        view.imageViewQrCode.setOnClickListener {
            QrDialog.show(childFragmentManager, getAddress())
        }
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

    private fun setupToolbar(view: View) {
        view.appbar.addOnOffsetChangedListener(this)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(view.toolbarResults)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = ""
        }
    }

    private fun setupRecyclerView(view: View) {
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        view.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
            adapter = null
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun setupSwipeToRefresh(view: View) {
        view.swiperefresh.apply {
            //TODO: load position automatically from layout (imgDetailsBitcoin)
            setProgressViewOffset(false, 0, requireContext().convertDpToPixel(58.5f))
            setOnRefreshListener {
                viewModel.refresh()
            }
        }
    }


    fun showEditDialog(address: String, nickname: String) {
//        EditDialog.newInstance(EditDialog.Parameters(address, nickname)).show(fragmentManager!!, EditDialog.TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val isFavorited = viewModel.walledDetailsViewState.value?.wallet?.favourite == true
        val isLoading = viewModel.walledDetailsViewState.value?.loading == true
        val isEditable = viewModel.walledDetailsViewState.value?.wallet != null
        menu.findItem(R.id.menu_refresh).isEnabled = !isLoading
        menu.findItem(R.id.menu_favourite).isVisible = isEditable && !isFavorited
        menu.findItem(R.id.menu_unfavourite).isVisible = isEditable && isFavorited
        menu.findItem(R.id.menu_edit).isVisible = isEditable
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                viewModel.refresh()
                true
            }
            R.id.menu_favourite -> {
                viewModel.walledDetailsViewState.value?.wallet?.let {
                    actionsViewModel.toggleFavourite(WalletRecordEntity.fromWalletRecord(it))
                }
                Toast.makeText(context, getString(R.string.address_favourited), Toast.LENGTH_SHORT)
                    .show()
                true
            }
            R.id.menu_unfavourite -> {
                viewModel.walledDetailsViewState.value?.wallet?.let {
                    actionsViewModel.toggleFavourite(WalletRecordEntity.fromWalletRecord(it))
                }
                Toast.makeText(
                    context,
                    getString(R.string.address_unfavourited),
                    Toast.LENGTH_SHORT
                ).show()
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
        private const val TAG = "DetailFragment"
        const val WALLET_ID = "WALLET_ID"

        fun open(fragmentManager: FragmentManager, walletID: String) {
            fragmentManager.commit {
                val bundle = bundleOf(WALLET_ID to walletID)
                setReorderingAllowed(true)
                addToBackStack(TAG)
                add(R.id.container, DetailFragment::class.java, bundle)
            }
        }
    }

}
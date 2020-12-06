package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.addadress.AddAddressDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.currency.CurrencyBottomSheetFragment
import com.jurajkusnier.bitcoinwalletbalance.ui.editdialog.EditDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_fragment.view.*

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment) {

    private val viewModel: MainViewModel by viewModels({ requireActivity() })
    private val actionsViewModel: ActionsViewModel by viewModels({ requireActivity() })
    private lateinit var fabComponent: FabComponent
    private lateinit var viewPagerComponent: ViewPagerComponent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.setSupportActionBar(view.toolbar)
        fabComponent = FabComponent(view, this::openAddAddressDialog, this::openAddAddressCamera)
        viewPagerComponent = ViewPagerComponent(view, this)
        viewModel.action.observe(viewLifecycleOwner, {
            if (it is Actions.OpenDetails) {
                openDetails(it.address)
            }
        })
    }

    private fun openDetails(address: String) {
        (activity as? MainActivity)?.openDetails(address)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        actionsViewModel.action.observe(viewLifecycleOwner, {
            when (it) {
                is ActionsViewModel.Actions.ItemDeleted -> showUndoSnackbar(getString(R.string.address_deleted)) {
                    actionsViewModel.recoverItem(it.address)
                }
                is ActionsViewModel.Actions.ShowEditDialog -> {
                    EditDialog.show(childFragmentManager, it.parameters)
                }
                is ActionsViewModel.Actions.ItemUnfavourited -> {
                }
                is ActionsViewModel.Actions.ItemFavourited -> {
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result?.contents != null) {

                var bitcoinAddress = result.contents
                val bitcoinAddressPrefix = getString(R.string.bitcoin_addr_prefix)

                if (bitcoinAddress.startsWith(bitcoinAddressPrefix, true)) {
                    bitcoinAddress = bitcoinAddress.substring(bitcoinAddressPrefix.length)
                }

                (activity as? MainActivity)?.openDetails(bitcoinAddress)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showUndoSnackbar(undoText: String, action: () -> Unit) {
        view?.let {
            Snackbar.make(it, undoText, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo)) { action() }
                .show()
        }
    }

    private fun openAddAddressDialog() {
        AddAddressDialog.show(childFragmentManager)
    }

    private fun openAddAddressCamera() {
        IntentIntegrator.forSupportFragment(this)
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setPrompt(getString(R.string.scan_qr_code))
            .setCameraId(0)
            .setBeepEnabled(false)
            .initiateScan()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_currency) {
            CurrencyBottomSheetFragment.show(childFragmentManager)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "MainFragment"
        fun newInstance() = MainFragment()
    }

}
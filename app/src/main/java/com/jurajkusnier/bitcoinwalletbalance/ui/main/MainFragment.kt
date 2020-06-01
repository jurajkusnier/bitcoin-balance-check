package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialogInterface
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.main_fragment.view.*
import javax.inject.Inject


abstract class MainFragment : DaggerFragment(), EditDialogInterface {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var rootView: View
    private lateinit var listAdapter: MainListAdapter
    protected lateinit var viewModel: MainViewModel

    abstract fun getViewModelClass(): Class<out ViewModel>

    abstract fun getEmptyText(): CharSequence

    override fun showEditDialog(address: String, nickname: String) {
        EditDialog.newInstance(EditDialog.Parameters(address, nickname)).show(fragmentManager!!, EditDialog.TAG)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.main_fragment, container, false)
        rootView.emptyListText.text = getEmptyText()
        rootView.textSpannableInfo.text = getSpannableInfo(rootView.context)
        return rootView
    }

    protected fun showUndoSnackbar(undoText: String, action: () -> Unit) {
        Snackbar.make(rootView, undoText, Snackbar.LENGTH_LONG).apply {
            setAction(getString(R.string.undo)) { action() }
            view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorSnackbar, null))
            show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(getViewModelClass()) as MainViewModel
        listAdapter = MainListAdapter(requireContext(), viewModel, this)

        viewModel.deletedAddress.observe(viewLifecycleOwner, Observer { deletedRecord ->
            showUndoSnackbar(getString(R.string.address_deleted)) {
                viewModel.recoverDeletedRecord(deletedRecord)
            }
        })

        viewModel.data.observe(viewLifecycleOwner, Observer<List<WalletRecordEntity>> dbObserver@{
            if (it.isNullOrEmpty()) {
                rootView.layoutEmptyList.visibility = View.VISIBLE
            } else {
                rootView.layoutEmptyList.visibility = View.GONE
            }
            listAdapter.submitList(it)
        })

        rootView.recyclerViewMain.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
            adapter = listAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun getSpannableInfo(context: Context): SpannableString {
        val text = SpannableString(getString(R.string.first_instruction))
        val addIcon = ContextCompat.getDrawable(context, R.drawable.ic_add_white_24dp)
                ?: return text
        val favIcon = ContextCompat.getDrawable(context, R.drawable.ic_favorite_white_24dp)
                ?: return text

        addIcon.setBounds(0, 0, (addIcon.intrinsicWidth * 0.9f).toInt(), (addIcon.intrinsicHeight * 0.9f).toInt())
        favIcon.setBounds(0, 0, (favIcon.intrinsicWidth * 0.9f).toInt(), (favIcon.intrinsicHeight * 0.9f).toInt())
        val spanAdd = ImageSpan(addIcon, ImageSpan.ALIGN_BOTTOM)
        val spanFav = ImageSpan(favIcon, ImageSpan.ALIGN_BOTTOM)
        val pAdd = text.indexOf('+')
        val pFav = text.indexOf('*')
        text.setSpan(spanAdd, pAdd, pAdd + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        text.setSpan(spanFav, pFav, pFav + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        return text
    }

    companion object {
        const val TAG = "MainFragment"
    }
}

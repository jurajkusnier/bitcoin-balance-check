package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialogInterface
import com.jurajkusnier.bitcoinwalletbalance.utils.DaggerLifecycleFragment
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.main_fragment.view.*
import javax.inject.Inject

abstract class MainFragment : DaggerLifecycleFragment(), EditDialogInterface {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private lateinit var _thisView:View
    protected lateinit var viewModel: MainViewModel

    abstract fun getViewModelClass():Class<out ViewModel>

    companion object {
        val TAG = MainFragment::class.java.simpleName
    }

    override fun onAttach(context: Context?) {
        //DI activity injection first
        AndroidInjection.inject(activity)
        super.onAttach(context)
    }

    private lateinit var adapter: MainListAdapter

    abstract fun getEmptyText():CharSequence

    protected fun showUndoSnackbar(deletedRecord: WalletRecord, undoText:String) {
        val snackbar = Snackbar.make(_thisView, undoText, Snackbar.LENGTH_LONG)
        snackbar.setAction(getString(R.string.undo), {
            viewModel.addRecord(deletedRecord)
        })
        snackbar.view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorSnackbar,null))
        snackbar.show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _thisView = inflater.inflate(R.layout.main_fragment, container, false)

        _thisView.emptyListText.text = getEmptyText()

        return _thisView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(getViewModelClass()) as MainViewModel

        adapter = MainListAdapter(context!!, viewModel,this)

        //Fragment Lifecycle Hack from https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
        val thisLifecycleOwner = getViewLifecycleOwner()
        if (thisLifecycleOwner != null) {

            viewModel.deletedItem.observe(thisLifecycleOwner, Observer deletedItemObserver@{
                deletedRecord ->
                    if (deletedRecord  == null) return@deletedItemObserver
                    showUndoSnackbar(deletedRecord, getString(R.string.address_deleted))
            })

            viewModel.data.observe(thisLifecycleOwner, Observer<List<WalletRecord>> dbObserver@{

                if (it == null || it.isEmpty()) {
                    _thisView.layoutEmptyList.visibility = View.VISIBLE
                } else {
                    _thisView.layoutEmptyList.visibility = View.GONE
                }

                adapter.submitList(it)
            })

        }

        val recyclerView = _thisView.recyclerViewMain

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        adapter.submitList(null)

        val mDividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(mDividerItemDecoration)
    }

    override fun showEditDialog(address:String, nickname:String) {
        EditDialog.newInstance(address,nickname).show(fragmentManager, EditDialog.TAG)
    }
}

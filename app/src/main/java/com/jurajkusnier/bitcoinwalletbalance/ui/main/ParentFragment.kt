package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.zxing.integration.android.IntentIntegrator
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.addadress.AddAddressDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.detail.DetailFragment
import com.jurajkusnier.bitcoinwalletbalance.ui.favourite.FavouriteFragment
import com.jurajkusnier.bitcoinwalletbalance.ui.history.HistoryFragment
import com.jurajkusnier.bitcoinwalletbalance.ui.settings.SettingsDialog
import kotlinx.android.synthetic.main.parent_fragment.*

class ParentFragment : Fragment() {

    val TAG = ParentFragment::class.java.simpleName

    val MY_CAMERA_PERMISSION = 123

    companion object {
        fun newInstance() = ParentFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.parent_fragment, container, false)
    }

    lateinit var fabOpenAnimation: Animation
    lateinit var fabCloseAnimation: Animation
    lateinit var fabRotateForwad: Animation
    lateinit var fabRotateBackward: Animation
    lateinit var backdropShow: Animation
    lateinit var backdropHide: Animation

    var isFabOpen = false

    fun animateFAB() {
        if (isFabOpen) {
            floatingButtonAdd.startAnimation(fabRotateBackward)
            floatingButtonAddManual.startAnimation(fabCloseAnimation)
            floatingButtonAddQr.startAnimation(fabCloseAnimation)
            floatingButtonAddManual.isClickable = false
            floatingButtonAddQr.isClickable = false
            fabBackdrop.isClickable = false
            fabBackdrop.isFocusable = false
            fabBackdrop.startAnimation(backdropHide)
        } else {
            floatingButtonAdd.startAnimation(fabRotateForwad)
            floatingButtonAddManual.startAnimation(fabOpenAnimation)
            floatingButtonAddQr.startAnimation(fabOpenAnimation)
            floatingButtonAddManual.isClickable = true
            floatingButtonAddQr.isClickable = true
            fabBackdrop.isClickable = true
            fabBackdrop.isFocusable = true
            fabBackdrop.startAnimation(backdropShow)
        }
        isFabOpen = !isFabOpen
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fabOpenAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabCloseAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        fabRotateForwad = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        fabRotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward)
        backdropShow = AnimationUtils.loadAnimation(context, R.anim.backdrop_show)
        backdropHide = AnimationUtils.loadAnimation(context, R.anim.backdrop_hide)

        activity?.let {
            if (it is AppCompatActivity) {
                initView(it)
            }
        }

        fabBackdrop.setOnClickListener {
            if (isFabOpen) animateFAB()
        }

        floatingButtonAddManual.setOnClickListener {
            animateFAB()
            AddAddressDialog.newInstance().show(fragmentManager!!, SettingsDialog.TAG)
        }

        floatingButtonAddQr.setOnClickListener {
            animateFAB()

            context?.let {
                if (ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION)
                } else {
                    startBarcodeScanner()
                }
            }
        }

        floatingButtonAdd.setOnClickListener {
            animateFAB()
        }

        setHasOptionsMenu(true)
    }

    private fun initView(context: AppCompatActivity) {

        context.setSupportActionBar(toolbar)

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.favourites)).setIcon(R.drawable.ic_favorite_white_24dp))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.history)).setIcon(R.drawable.ic_history_white_24dp))

        val adapter = PagerAdapter(childFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter
        tabLayout.tabTextColors
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                tab.icon?.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorTabActive, null), PorterDuff.Mode.SRC_ATOP)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.icon?.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorTabInactive, null), PorterDuff.Mode.SRC_ATOP)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        tabLayout.getTabAt(1)?.icon?.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorTabInactive, null), PorterDuff.Mode.SRC_ATOP)
    }

    class PagerAdapter(private val fm: FragmentManager, private val NumOfTabs: Int) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    FavouriteFragment.newInstance()
                }
                else -> {
                    HistoryFragment.newInstance()
                }
            }
        }

        override fun getCount(): Int {
            return NumOfTabs
        }

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

                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container, DetailFragment.newInstance(bitcoinAddress))
                        ?.addToBackStack(DetailFragment.TAG)
                        ?.commit()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_CAMERA_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBarcodeScanner()
            }
        }
    }

    private fun startBarcodeScanner() {
        val intentIntegrator = IntentIntegrator.forSupportFragment(this)
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        intentIntegrator.setPrompt(getString(R.string.scan_qr_code))
        intentIntegrator.setCameraId(0)  // Use a specific camera of the device
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.setBarcodeImageEnabled(false)
        intentIntegrator.setOrientationLocked(true)
        intentIntegrator.initiateScan()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.parent_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
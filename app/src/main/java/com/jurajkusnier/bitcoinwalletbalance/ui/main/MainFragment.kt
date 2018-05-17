package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.integration.android.IntentIntegrator
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.detail.DetailFragment
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    val TAG = MainFragment::class.java.simpleName

    val MY_CAMERA_PERMISSION = 123

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        floatingButtonAdd.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context!!,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity!!,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_PERMISSION)
            } else {
                startBarcodeScanner()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result= IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result?.contents != null) {

                var bitcoinAddress= result.contents
                val bitcoinAddressPrefix = getString(R.string.bitcoin_addr_prefix)

                if (bitcoinAddress.startsWith(bitcoinAddressPrefix,true)) {
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
        if (requestCode == MY_CAMERA_PERMISSION ) {
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
}
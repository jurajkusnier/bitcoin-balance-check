package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.view.View
import android.view.animation.AnimationUtils
import com.jurajkusnier.bitcoinwalletbalance.R
import kotlinx.android.synthetic.main.main_fragment.view.*

class FabComponent(private val view: View, val onAddManual: () -> Unit, val onAddQr: () -> Unit) {

    private val fabOpenAnimation = AnimationUtils.loadAnimation(view.context, R.anim.fab_open)
    private val fabCloseAnimation = AnimationUtils.loadAnimation(view.context, R.anim.fab_close)
    private val fabRotateForwad = AnimationUtils.loadAnimation(view.context, R.anim.rotate_forward)
    private val fabRotateBackward = AnimationUtils.loadAnimation(view.context, R.anim.rotate_backward)
    private val backdropShow = AnimationUtils.loadAnimation(view.context, R.anim.backdrop_show)
    private val backdropHide = AnimationUtils.loadAnimation(view.context, R.anim.backdrop_hide)

    private var isFabOpen = false

    init {
        view.apply {
            fabBackdrop.setOnClickListener {
                if (isFabOpen) toggleFloatingActionButtons()
            }

            floatingButtonAddManual.setOnClickListener {
                toggleFloatingActionButtons()
                onAddManual()
            }

            floatingButtonAddQr.setOnClickListener {
                toggleFloatingActionButtons()
                onAddQr()
            }

            floatingButtonAdd.setOnClickListener {
                toggleFloatingActionButtons()
            }
        }
    }

    private fun toggleFloatingActionButtons() {
        view.apply {
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
        }
        isFabOpen = !isFabOpen
    }


}
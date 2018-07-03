package com.jurajkusnier.bitcoinwalletbalance.ui.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.utils.CustomDate
import com.jurajkusnier.bitcoinwalletbalance.utils.format
import javax.inject.Inject

class SettingViewModel @Inject constructor(private val settingRepository: SettingRepository, private val customDate: CustomDate) :ViewModel() {

    val TAG = SettingViewModel::class.java.simpleName

    data class ExchangeDetails(val exchangeRate: String,val lastUpdate:String)

    enum class LoadingState {DONE, LOADING, ERROR}

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState:LiveData<LoadingState>
        get() = _loadingState

    private val _currencyRate = MutableLiveData<ExchangeDetails>()
    val currencyRate:LiveData<ExchangeDetails>
        get() = _currencyRate

    fun changeCurrency(id:Int) = settingRepository.conversionPrefs.changeCurrency(id)

    fun getCurrencyId():Int = settingRepository.conversionPrefs.getCurrencyIndex()

    fun changeCurrencyPreview(newCurrencyCode:String? = null) {
        val oldCurrencyCode = newCurrencyCode?:settingRepository.conversionPrefs.getCurrencyCode()

        _loadingState.value = LoadingState.LOADING

        settingRepository.getBitcoinPrice(oldCurrencyCode, object : SettingRepository.PriceLoaderCallback {
            override fun setResults(currencyRate: Float, currencyCode: String, lastUpdate: Long) {
                _currencyRate.postValue (ExchangeDetails("1 BTC = ${currencyRate.format(2)} $currencyCode",customDate.getLastUpdatedString(lastUpdate)))
            }

            override fun loadingDone() {
                _loadingState.postValue (LoadingState.DONE)
            }

            override fun loadingFailed() {
                _loadingState.postValue (LoadingState.ERROR)
            }
        })
    }
}
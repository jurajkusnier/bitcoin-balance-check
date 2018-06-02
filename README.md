# Wealth Check - Bitcoin Wallet Balance and History
This project is based on **Android App Template** from https://github.com/jurajkusnier/android-app-template  


## About
Wealth Check allows you to check balance an history of any bitcoin wallet. Just scan bitcoin address QR code to see all important information like bitcoin balance, fiat balance by current conversation rate, transactions history, etc. All scanned wallets are safely stored on your phone for offline access. You can sort wallets in the favorites list.

![Screenshot](https://github.com/jurajkusnier/bitcoin-balance-check/raw/master/screenshots/device-2018-06-02-223449.png | width=200)
![Screenshot](https://github.com/jurajkusnier/bitcoin-balance-check/raw/master/screenshots/device-2018-06-02-223501.png | width=200)
![Screenshot](https://github.com/jurajkusnier/bitcoin-balance-check/raw/master/screenshots/device-2018-06-02-223418.png | width=200)

## Codebase
The project is written in Kotlin. Following MVVM architecture and using Android Jetpack libraries like ViewModel, Room, LiveData etc. Project is structured to modules divided by features. Dependencies between modules are solved by dependency injections with Dagger 2.   

## Libraries
 * **Android Architecture Components** - https://developer.android.com/topic/libraries/architecture/
 * **Dagger 2** - https://github.com/google/dagger
 * **Retrofit 2** - http://square.github.io/retrofit
 * **OkHttp** - http://square.github.io/okhttp
 * **RxAndroid** - https://github.com/ReactiveX/RxAndroid
 * **RxJava** - https://github.com/ReactiveX/RxJava
 * **Moshi** - https://github.com/square/moshi
 * **Room** - https://developer.android.com/topic/libraries/architecture/room
 * **ZXing Barcode Scanner** - https://github.com/journeyapps/zxing-android-embedded
 
 ## Disclaimer
 App is still in development. Some features are not implemented yet.


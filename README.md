# Wealth Check - Bitcoin Wallet Balance and History

Wealth Check allows you to check balance an history of any bitcoin wallet. Just scan bitcoin address QR code to see all important information like bitcoin balance, fiat balance by current conversation rate, transactions history, etc. All scanned wallets are safely stored on your phone for offline access. You can sort wallets in the favorites list.

![Screenshots](https://github.com/jurajkusnier/bitcoin-balance-check/raw/master/screenshots/screenshots.png)

## Codebase
The project is written in Kotlin. Following MVVM architecture and using Android Jetpack libraries like ViewModel, Room, LiveData etc. Project is structured to modules divided by features. Dependencies between modules are solved by dependency injections with Hilt.   

## Libraries
 * **Android Architecture Components** - https://developer.android.com/topic/libraries/architecture/
 * ~~**Dagger 2** - https://github.com/google/dagg~~
 * **Hilt** - https://dagger.dev/hilt/
 * **Retrofit 2** - http://square.github.io/retrofit
 * **OkHttp** - http://square.github.io/okhttp
 * ~~**RxAndroid** - https://github.com/ReactiveX/RxAndroid~~
 * ~~**RxJava** - https://github.com/ReactiveX/RxJava~~
 * **Coroutines** - https://kotlinlang.org/docs/reference/coroutines-overview.html
 * **Moshi** - https://github.com/square/moshi
 * **Room** - https://developer.android.com/topic/libraries/architecture/room
 * **ZXing Barcode Scanner** - https://github.com/journeyapps/zxing-android-embedded
 
 ## Google Play
 App is available on Google Play Store https://play.google.com/store/apps/details?id=com.jurajkusnier.bitcoinwalletbalance


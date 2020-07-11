package com.ncloudtech.crypto

import android.app.Application
import ru.CryptoPro.JCSP.CSPConfig
import timber.log.Timber

class CryptoProApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val cryptoProInitError = CSPConfig.init(this)
        Timber.plant(Timber.DebugTree())
        Timber.d("Crypto pro initialized with status: $cryptoProInitError")
    }
}
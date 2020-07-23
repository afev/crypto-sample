package com.ncloudtech.crypto

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ru.CryptoPro.ACSPClientApp.client.example.HttpsUrlConnectionExample
import ru.CryptoPro.ACSPClientApp.client.example.HttpsUrlConnectionSimpleExample
import ru.CryptoPro.ACSPClientApp.client.example.OkHttpExample
import ru.CryptoPro.ACSPClientApp.client.example.OkHttpSimpleExample
import ru.CryptoPro.ACSPClientApp.util.ContainerAdapter
import ru.CryptoPro.ACSPClientApp.util.ProviderType
import ru.CryptoPro.ACSPClientApp.util.RemoteConnectionInfo
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            runExample()
        }
    }

    private fun runExample() {


        val adapter =
            ContainerAdapter(this, null, false)
        adapter.providerType = ProviderType.currentProviderType()
        adapter.connectionInfo = RemoteConnectionInfo.test
        adapter.trustStorePassword = "".toCharArray()

        try {


            HttpsUrlConnectionSimpleExample(adapter).getResult {
//            OkHttpSimpleExample(adapter).getResult {
                println("Finished")
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}

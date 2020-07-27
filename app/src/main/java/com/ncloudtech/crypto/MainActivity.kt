package com.ncloudtech.crypto

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import ru.CryptoPro.ACSPClientApp.client.example.HttpsUrlConnectionExample
import ru.CryptoPro.ACSPClientApp.client.example.HttpsUrlConnectionSimpleExample
import ru.CryptoPro.ACSPClientApp.client.example.OkHttpExample
import ru.CryptoPro.ACSPClientApp.client.example.OkHttpSimpleExample
import ru.CryptoPro.ACSPClientApp.util.ContainerAdapter
import ru.CryptoPro.ACSPClientApp.util.ProviderType
import ru.CryptoPro.ACSPClientApp.util.RemoteConnectionInfo
import ru.CryptoPro.JCP.tools.Decoder
import ru.CryptoPro.JCSP.support.BKSTrustStore
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            runExample()
        }
    }

    private fun runExample() {

        val trusted_root_for_cpca =
            "MIIDhzCCAvOgAwIBAgIRAnioGgH3qu28QYxppLJocdYwCgYIKoUDBwEBAwMwgbcx" +
            "IDAeBgkqhkiG9w0BCQEWEWNwY2FAY3J5cHRvcHJvLnJ1MQswCQYDVQQGEwJSVTEV" +
            "MBMGA1UECAwM0JzQvtGB0LrQstCwMRUwEwYDVQQHDAzQnNC+0YHQutCy0LAxJTAj" +
            "BgNVBAoMHNCe0J7QniAi0JrQoNCY0J/QotCeLdCf0KDQniIxMTAvBgNVBAMMKNCj" +
            "0KYg0JrQoNCY0J/QotCeLdCf0KDQniAo0JPQntCh0KIgMjAxMikwHhcNMTkxMDMx" +
            "MTY1OTA4WhcNMzQxMDMxMTY1OTA4WjCBtzEgMB4GCSqGSIb3DQEJARYRY3BjYUBj" +
            "cnlwdG9wcm8ucnUxCzAJBgNVBAYTAlJVMRUwEwYDVQQIDAzQnNC+0YHQutCy0LAx" +
            "FTATBgNVBAcMDNCc0L7RgdC60LLQsDElMCMGA1UECgwc0J7QntCeICLQmtCg0JjQ" +
            "n9Ci0J4t0J/QoNCeIjExMC8GA1UEAwwo0KPQpiDQmtCg0JjQn9Ci0J4t0J/QoNCe" +
            "ICjQk9Ce0KHQoiAyMDEyKTCBqjAhBggqhQMHAQEBAjAVBgkqhQMHAQIBAgEGCCqF" +
            "AwcBAQIDA4GEAASBgKmcZO/mbqp0F2nO5Ars3b/nOVJlezPUxAw+Y6w6CCZAViL1" +
            "u2FZujutdBd22EUWQiespuIaze8TJBsPRlThtSiqN4jVjCibIbLVO0ENEACKR84J" +
            "nmva2s0AvyOJkN3kWFUfxm4AXiHlD992IU63QuNk+Xk4QbqwLcAv7TTmcs8ho4GL" +
            "MIGIMA4GA1UdDwEB/wQEAwIBhjAdBgNVHQ4EFgQULw8w7hsuk9ribYNd8CY2uBGU" +
            "ht0wDwYDVR0TAQH/BAUwAwEB/zARBgkrBgEEAYI3FAIEBAwCQ0EwEgYJKwYBBAGC" +
            "NxUBBAUCAwIAAjAfBgkrBgEEAYI3FQcEEjAQBggqhQMCAi4AAAIBAQIBADAKBggq" +
            "hQMHAQEDAwOBgQBG2v8I7rt963Gn4s92kLdcIf0Omfayj2UvE2Rn4hfjBs7QIrPV" +
            "8/t9jZO5plPylJuna6D56+XYkbBovS1RqSx2QaRiiKcTRUFBcDOAeatohNsj/pLp" +
            "lgTFrWS/qqaxd+uJlmlyvaTxRHuAkR2RwhGR4UcyZ6NWbtOGXWKU/l5bNg==";

        val adapter =
            ContainerAdapter(this, null, false)
        adapter.providerType = ProviderType.currentProviderType()
        adapter.connectionInfo = RemoteConnectionInfo.test
        adapter.trustStoreProvider = BouncyCastleProvider.PROVIDER_NAME
        adapter.trustStoreType = BKSTrustStore.STORAGE_TYPE
        adapter.trustStorePassword = BKSTrustStore.STORAGE_PASSWORD
        val trustStorePath =
            applicationInfo.dataDir.toString() + File.separator +
                    BKSTrustStore.STORAGE_DIRECTORY + File.separator + BKSTrustStore.STORAGE_FILE_TRUST

        val trustStore = KeyStore.getInstance(adapter.trustStoreType, adapter.trustStoreProvider);

        val trustFile = File(trustStorePath);
        trustStore.load(FileInputStream(trustFile), adapter.trustStorePassword);

        val cert_bin  = (Decoder()).decodeBuffer(trusted_root_for_cpca);
        val cert_root = CertificateFactory.getInstance("X.509").generateCertificate(ByteArrayInputStream(cert_bin));

        if (trustStore.getCertificateAlias(cert_root) == null) {
            Timber.i("Adding a new root certificate...");
            trustStore.setCertificateEntry("trusted_root_for_cpca", cert_root);
        }
        else {
            Timber.i("Certificate already exists.");
        }

        trustStore.store(FileOutputStream(trustFile), adapter.trustStorePassword);

        val trustStoreStream = FileInputStream(trustStorePath)
        adapter.trustStoreStream = trustStoreStream

        try {


//            HttpsUrlConnectionSimpleExample(adapter).getResult {
            OkHttpSimpleExample(adapter).getResult {
                println("Finished")
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}

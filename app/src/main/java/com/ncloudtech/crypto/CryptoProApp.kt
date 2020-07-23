package com.ncloudtech.crypto

import android.app.Application
import android.util.Log
import org.apache.xml.security.utils.resolver.ResourceResolver
import ru.CryptoPro.AdES.AdESConfig
import ru.CryptoPro.JCPxml.XmlInit
import ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI
import ru.CryptoPro.JCSP.CSPConfig
import ru.CryptoPro.JCSP.JCSP
import ru.CryptoPro.JCSP.support.BKSTrustStore
import ru.CryptoPro.reprov.RevCheck
import ru.CryptoPro.ssl.android.util.cpSSLConfig
import ru.cprocsp.ACSP.tools.common.Constants
import timber.log.Timber
import java.io.File
import java.security.Provider
import java.security.Security

class CryptoProApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val cryptoProInitError = CSPConfig.initEx(this)
        Timber.plant(Timber.DebugTree())
        Timber.d("Crypto pro initialized with status: $cryptoProInitError")

        initJavaProviders()
    }

    private fun initJavaProviders() { // %%% Инициализация остальных провайдеров %%%
//
// 1. Загрузка Java CSP (хеш, подпись, шифрование,
// генерация контейнеров).
//
        if (Security.getProvider(JCSP.PROVIDER_NAME) == null) {
            Security.addProvider(JCSP())
        } // if
        //
// 2. Загрузка Java TLS (TLS).
//
// Необходимо переопределить свойства, чтобы использовались менеджеры
// из cpSSL, а не Harmony.
//
// Внимание!
// Чтобы не мешать не-ГОСТовой реализации, ряд свойств внизу *.ssl и
// javax.net.* НЕ следует переопределять. Но при этом не исключены проблемы
// в работе с ГОСТом там, где TLS-реализация клиента обращается к дефолтным
// алгоритмам реализаций этих factory (особенно: apache http client или
// HttpsURLConnection без передачи SSLSocketFactory).
// Здесь эти свойства включены, т.к. пример УЦ 1.5 использует алгоритмы
// по умолчанию.
//
// Если инициализировать провайдер в CSPConfig с помощью initEx(), то
// свойства будут включены там, поэтому выше используется упрощенная
// версия инициализации.
//
        Security.setProperty(
            "ssl.KeyManagerFactory.algorithm",
            ru.CryptoPro.ssl.android.Provider.KEYMANGER_ALG
        )
        Security.setProperty(
            "ssl.TrustManagerFactory.algorithm",
            ru.CryptoPro.ssl.android.Provider.KEYMANGER_ALG
        )
        Security.setProperty(
            "ssl.SocketFactory.provider",
            "ru.CryptoPro.ssl.android.SSLSocketFactoryImpl"
        )
        Security.setProperty(
            "ssl.ServerSocketFactory.provider",
            "ru.CryptoPro.ssl.android.SSLServerSocketFactoryImpl"
        )
        if (Security.getProvider(ru.CryptoPro.ssl.android.Provider.PROVIDER_NAME) == null) {
            Security.addProvider(ru.CryptoPro.ssl.android.Provider())
        } // if
        //
// 3. Провайдер хеширования, подписи, шифрования
// по умолчанию.
//
        cpSSLConfig.setDefaultSSLProvider(JCSP.PROVIDER_NAME)
        //
// 4. Загрузка Revocation Provider (CRL, OCSP).
//
        if (Security.getProvider(RevCheck.PROVIDER_NAME) == null) {
            Security.addProvider(RevCheck())
        } // if
        //
// 5. Отключаем проверку цепочки штампа времени (CAdES-T),
// чтобы не требовать него CRL.
//
        System.setProperty("ru.CryptoPro.CAdES.validate_tsp", "false")
        //
// 6. Таймауты для CRL на всякий случай.
//
        System.setProperty("com.sun.security.crl.timeout", "5")
        System.setProperty("ru.CryptoPro.crl.read_timeout", "5")
        // 7. Задание провайдера по умолчанию для CAdES.
        AdESConfig.setDefaultProvider(JCSP.PROVIDER_NAME)
        // 8. Инициализация XML DSig (хеш, подпись).
        XmlInit.init()
        // Добавление реализации поиска узла по ID.
        ResourceResolver.registerAtStart(XmlInit.JCP_XML_DOCUMENT_ID_RESOLVER)
        // Добавление XMLDSigRI провайдера, так как его
// использует XAdES.
        val xmlDSigRi: Provider = XMLDSigRI()
        Security.addProvider(xmlDSigRi)
        val provider = Security.getProvider("XMLDSig")
        if (provider != null) {
            Security.getProvider("XMLDSig")["XMLSignatureFactory.DOM"] =
                "ru.CryptoPro.JCPxml.dsig.internal.dom.DOMXMLSignatureFactory"
            Security.getProvider("XMLDSig")["KeyInfoFactory.DOM"] =
                "ru.CryptoPro.JCPxml.dsig.internal.dom.DOMKeyInfoFactory"
        } // if
        // 9. Включаем возможность онлайновой проверки статуса
// сертификата.
//
// Для TLS проверку цепочки сертификатов другой стороны
// можно отключить, если создать параметр
// Enable_revocation_default=false в файле android_pref_store
// (shared preferences), см.
// {@link ru.CryptoPro.JCP.tools.pref_store#AndroidPrefStore}.
        System.setProperty("com.sun.security.enableCRLDP", "true")
        System.setProperty("com.ibm.security.enableCRLDP", "true")
        // Отключаем требование проверки сертификата и хоста.
        System.setProperty("tls_prohibit_disabled_validation", "false")
        //
// 10. Настройки TLS для генерации контейнера и выпуска сертификата
// в УЦ 2.0, т.к. обращение к УЦ 2.0 будет выполняться по протоколу
// HTTPS и потребуется авторизация по сертификату. Указываем тип
// хранилища с доверенным корневым сертификатом, путь к нему и пароль.
//
// Внимание!
// Чтобы не мешать не-ГОСТовой реализации, ряд свойств внизу *.ssl и
// javax.net.* НЕ следует переопределять. Но при этом не исключены проблемы
// в работе с ГОСТом там, где TLS-реализация клиента обращается к дефолтным
// алгоритмам реализаций этих factory (особенно: apache http client или
// HttpsURLConnection без передачи SSLSocketFactory).
// Здесь эти свойства включены, т.к. пример УЦ 1.5 использует алгоритмы
// по умолчанию.
//
// Здесь эти свойства включены, т.к. пример УЦ 1.5 использует алгоритмы
// по умолчанию. Примеров УЦ 2.0 пока нет.
//
        val trustStorePath =
            applicationInfo.dataDir.toString() + File.separator +
                    BKSTrustStore.STORAGE_DIRECTORY + File.separator + BKSTrustStore.STORAGE_FILE_TRUST
        val trustStorePassword = String(BKSTrustStore.STORAGE_PASSWORD)
        Log.d(
            Constants.APP_LOGGER_TAG,
            "Default trust store: $trustStorePath"
        )
        System.setProperty("javax.net.ssl.trustStoreType", BKSTrustStore.STORAGE_TYPE)
        System.setProperty("javax.net.ssl.trustStore", trustStorePath)
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword)
    }
}
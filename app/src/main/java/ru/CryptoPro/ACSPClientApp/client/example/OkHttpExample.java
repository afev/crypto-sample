/**
 * $RCSfileOkHttpExample.java,v $
 * version $Revision: 36379 $
 * created 17.02.2020 15:05 by afevma
 * last modified $Date: 2012-05-30 12:19:27 +0400 (Ср, 30 май 2012) $ by $Author: afevma $
 * (C) ООО Крипто-Про 2004-2020.
 * <p/>
 * Программный код, содержащийся в этом файле, предназначен
 * для целей обучения. Может быть скопирован или модифицирован
 * при условии сохранения абзацев с указанием авторства и прав.
 * <p/>
 * Данный код не может быть непосредственно использован
 * для защиты информации. Компания Крипто-Про не несет никакой
 * ответственности за функционирование этого кода.
 */
package ru.CryptoPro.ACSPClientApp.client.example;

import java.util.Collections;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.CryptoPro.ACSPClientApp.client.example.base.FinalListener;
import ru.CryptoPro.ACSPClientApp.client.example.base.TLSData;
import ru.CryptoPro.ACSPClientApp.client.example.interfaces.ThreadExecuted;
import ru.CryptoPro.ACSPClientApp.util.ContainerAdapter;
import ru.CryptoPro.ACSPClientApp.util.Logger;
import timber.log.Timber;

/**
 * Класс OkHttpExample реализует пример обмена
 * по TLS 1.0 с помощью Ok Http v3.
 *
 * @author Copyright 2004-2020 Crypto-Pro. All rights reserved.
 * @.Version
 */
public class OkHttpExample extends TLSData {

    /**
     * Конструктор.
     *
     * @param adapter Настройки примера.
     */
    protected OkHttpExample(ContainerAdapter adapter) {
        super(adapter);
    }

    @Override
    public void getResult(FinalListener listener) throws Exception {

        OkHttpThread thread = new OkHttpThread();
        thread.addFinalListener(listener);

        getThreadResult(thread);

    }

    /**
     * Класс SimpleTLSThread реализует подключение
     * самописного клиента по TLS в отдельном потоке.
     *
     */
    private class OkHttpThread extends ThreadExecuted {

        @Override
        protected void executeOne() throws Exception {

            Logger.log("Init OkHttp Sample Example.");
            TrustManager[] trustManagers = new TrustManager[1];

            Logger.log("Create SSL context.");
            SSLContext sslContext = createSSLContext(trustManagers);

            Logger.log("Create SSL socket factory.");

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            // Установка нужного SSLSocketFactory.

            Logger.log("Create Ok Http client.");

            OkHttpClient.Builder builder  = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, trustManager);

            // Задание необходимых параметров (сюиты, протокол).

            ConnectionSpec spec = new ConnectionSpec.Builder(
                ConnectionSpec.MODERN_TLS)
                .tlsVersions("TLSv1")
                .cipherSuites(
                    "TLS_CIPHER_2012",
                    "TLS_CIPHER_2001")
                .build();

            builder.connectionSpecs(Collections.singletonList(spec));
            OkHttpClient client = builder.build();

            // Создание запроса к нужному адресу.

            Logger.log("Prepare request.");

            String uri = containerAdapter.getConnectionInfo().toUrl();
            Request request = new Request.Builder().url(uri).build();

            // Обращение к серверу и вывод полученного ответа.

            Logger.log("Send request.");

            Response response = client.newCall(request).execute();
            Logger.log("Successful: " + response.isSuccessful());

            logData(response.body().byteStream());
            // Logger.log("Connection has been established (OK).");
            Timber.i(">>>>>> OkHttp connection has been established (OK). >>>>>>");

        }

    }

}

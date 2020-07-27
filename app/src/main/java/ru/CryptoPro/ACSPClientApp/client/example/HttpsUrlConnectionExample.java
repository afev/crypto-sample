/**
 * $RCSfileHttpsUrlConnectionExample.java,v $
 * version $Revision: 36379 $
 * created 09.11.2017 11:02 by afevma
 * last modified $Date: 2012-05-30 12:19:27 +0400 (Ср, 30 май 2012) $ by $Author: afevma $
 * (C) ООО Крипто-Про 2004-2017.
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

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import ru.CryptoPro.ACSPClientApp.client.example.base.FinalListener;
import ru.CryptoPro.ACSPClientApp.client.example.base.NoSSLv3SocketFactory;
import ru.CryptoPro.ACSPClientApp.client.example.base.TLSData;
import ru.CryptoPro.ACSPClientApp.client.example.interfaces.ThreadExecuted;
import ru.CryptoPro.ACSPClientApp.util.ContainerAdapter;
import ru.CryptoPro.ACSPClientApp.util.Logger;
import timber.log.Timber;

/**
 * Класс HttpsUrlConnectionExample реализует пример обмена
 * по TLS 1.0 с использованием HttpsUrlConnection.
 *
 * @author Copyright 2004-2017 Crypto-Pro. All rights reserved.
 * @.Version
 */
public class HttpsUrlConnectionExample extends TLSData {

    /**
     * Конструктор.
     *
     * @param adapter Настройки примера.
     */
    protected HttpsUrlConnectionExample(ContainerAdapter adapter) {
        super(adapter);
    }

    @Override
    public void getResult(FinalListener listener) throws Exception {

        HttpsUrlConnectionThread thread = new HttpsUrlConnectionThread();
        thread.addFinalListener(listener);

        getThreadResult(thread);

    }

    /**
     * Класс SimpleTLSThread реализует подключение
     * HttpsUrlConnection клиента по TLS в отдельном
     * потоке.
     *
     */
    private class HttpsUrlConnectionThread extends ThreadExecuted {

        @Override
        protected void executeOne() throws Exception {

            HttpsURLConnection connection = null;
            Logger.log("Init HttpsURLConnection Example.");

            try {

                String httpAddress = containerAdapter.getConnectionInfo().toUrl();
                URL url = new URL(httpAddress);

                Logger.log("Create SSL context.");
                SSLContext sslContext = createSSLContext();

                Logger.log("Create SSL socket factory.");
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                SSLSocketFactory delegateFactory = new NoSSLv3SocketFactory(sslSocketFactory);
                Logger.log("Create connection.");

                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(delegateFactory);

                Logger.log("Connect.");
                connection.connect();

                Logger.log("Read input stream.");
                logContent(connection);

                // Logger.log("Connection has been established (OK).");
                Timber.i(">>>>>> Connection has been established (OK). >>>>>>");

            } finally {

                if (connection != null) {
                    connection.disconnect();
                } // if

            }
            /*
            try {

                URL url = new URL("https://ya.ru");
                Logger.log("Create connection.");

                connection = (HttpsURLConnection) url.openConnection();

                Logger.log("Connect.");
                connection.connect();

                Logger.log("Read input stream.");
                logContent(connection);

                Logger.log("Connection has been established (OK).");

            } finally {

                if (connection != null) {
                    connection.disconnect();
                } // if

            }
            */
        }

        /**
         * Вывод полеыенных данных.
         *
         * @param connection Подключение.
         * @throws Exception
         */
        private void logContent(HttpsURLConnection connection)
            throws Exception {

            if (connection != null) {

                Logger.log("Chosen cipher suite: " + connection.getCipherSuite());
                logData(connection.getInputStream());

            } // if

        }

    }

}

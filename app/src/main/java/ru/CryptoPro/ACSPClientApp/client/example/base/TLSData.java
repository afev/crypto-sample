/**
 * Copyright 2004-2013 Crypto-Pro. All rights reserved.
 * Программный код, содержащийся в этом файле, предназначен
 * для целей обучения. Может быть скопирован или модифицирован
 * при условии сохранения абзацев с указанием авторства и прав.
 *
 * Данный код не может быть непосредственно использован
 * для защиты информации. Компания Крипто-Про не несет никакой
 * ответственности за функционирование этого кода.
 */
package ru.CryptoPro.ACSPClientApp.client.example.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import ru.CryptoPro.ACSPClientApp.util.Constants;
import ru.CryptoPro.ACSPClientApp.util.ContainerAdapter;
import ru.CryptoPro.ACSPClientApp.util.KeyStoreType;
import ru.CryptoPro.ACSPClientApp.util.Logger;
import ru.CryptoPro.JCSP.JCSP;
import ru.CryptoPro.ssl.Provider;
import ru.CryptoPro.ssl.util.TLSContext;

/**
 * Служебный класс TLSData предназначен для
 * реализации примеров соединения по TLS.
 *
 * 30/05/2013
 *
 */
public abstract class TLSData extends EncryptDecryptData {

    /**
     * Конструктор.
     *
     * @param adapter Настройки примера.
     */
    protected TLSData(ContainerAdapter adapter) {
        super(adapter); // ignore
    }

    /*
    * Создание SSL контекста.
    *
    * @return готовый SSL контекст.
    * @throws Exception.
    */
    protected SSLContext createSSLContext() throws Exception {
        return createSSLContext(null);
    }

    /*
    * Создание SSL контекста.
    *
    * @param trustManagers Менеджеры хранилища доверенных
    * сертификатов. Для получения одного менеджера должен
    * быть передан массив по крайней мере из одного элемента,
    * в него будет помещен выбранный менеджер сертификатов.
    * Может быть null.
    * @return готовый SSL контекст.
    * @throws Exception.
    */
    protected SSLContext createSSLContext(TrustManager[]
        trustManagers) throws Exception {

        containerAdapter.printConnectionInfo();
        Logger.log("Create SSL context...");

        /**
         * Для чтения(!) доверенного хранилища доступна
         * реализация CertStore из Java CSP. В ее случае
         * можно не использовать пароль.
         */

        String keyStoreType = KeyStoreType.currentType();
        Logger.log("Init key store. Load containers. " +
            "Default container type: " + keyStoreType);

        // @see {@link ru.CryptoPro.ACSPClientApp.client.example.base#TLSData}
        // @see {@link ru.CryptoPro.ACSPInClientApp.examples#HttpsExample}
        // @see {@link ru.CryptoPro.ssl.android.util#TLSContext}
        //
        // В данном примере используется только функции из "белого" списка
        // из класса TLSContext и в случае применения двукхфакторной
        // аутентификации (2ФА) при инициализации TLS задается явный алиаса
        // ключа keyAlias!
        //
        // В случае 2ФА оптимальный вариант - всегда задавать алиас ключа,
        // т.к. это позволяет указать точно, какой контейнер использовать,
        // избегнув, возможно, долгого перечисления контейнеров.
        //
        // Есть 2 способа работы:
        // 1. "белый список" с функциями из класса TLSContext - для случаев
        // 1ФА и 2ФА;
        // 2. стандартный (прежний) Java SSE, использующий классы javax.net.ssl.*
        // или System.setProperty с передачей параметров аутентификации с
        // помощью свойств javax.net.ssl.*.
        //
        // Важно!
        // Рекомендуется использовать вариант 1!
        //
        // Важно!
        // Смешанное использование 1 и 2 вариантов крайне не рекомендуется: нужно
        // применять либо только первый подход, либо только второй!
        //
        // В первом случае SSLContext будет получен из функций п.1. В случае 2ФА
        // пароль к контейнеру в сами функции "белого" списка не передается, т.к.
        // он будет запрошен в ходе подбора контейнеров в специальном окне. Алиас
        // нужно передавать всегда, иначе пароль будет запрошен для всех найденных
        // контейнеров.
        //
        // Во втором случае (стандартный Java SSE) при 2ФА поведение осталось тем
        // же, что и раньше, то есть можно и дальше передать пароль в:
        // <KeyManagerFactory>.init(KeyStore, password)
        // в виде password, но появилась особенность из-за п.1: если пароль не
        // передали (т.е. null), то будет отображено окно ввода пароля для каждого(!)
        // контейнера, который может быть получен с помощью KeyStore, переданного в
        // KeyManagerFactory. Таким образом, в случае передачи пустого пароля (null)
        // также настоятельно рекомендуется передавать в KeyStore алиас с помощью
        // класса StoreInputStream в случае 2ФА в конструкции вида:
        // keyStore.load(new StoreInputStream(keyAlias), null) // задаем keyAlias
        // <KeyManagerFactory>.init(keyStore, password)
        //
        // Если же используется вариант с передачей параметров javax.net.ssl.* через
        // System.setProperty, то также настоятельно рекомендуется передавать
        // алиас с помощью:
        // System.setProperty("javax.net.ssl.keyStore", keyAlias);
        // в случае 2ФА.
        //
        // Важно!
        // Известно, что настройки вида System.setProperty со свойствами javax.net.ssl.*
        // обычно используются для разовой настройки дефолтного контекста, который
        // часто используют реализции HttpsURLConnection и т.п. в тех случаях, когда
        // не задали явно иной SSLSocketFactory для реализации TLS подключения.
        // В текущей реализации cpSSL такой контекст будет создан не полностью(!) и
        // заданный (сформированный из параметров) KeyStore загружен в него не будет(!),
        // т.к. использование дефолтного контекста ведет к появлению окон ввода пароля
        // для каждого контейнера, поскольку дефолтному контексту обычно известно об
        // алиасе и пароле из настроек вида System.setProperty, а такие настройки
        // задаются разово и распространяются на весь процесс, что может мешать
        // параллельной работе с неколькими контейнерами в случе 2ФА.
        // Теперь использование дефолтного контекста контролируется параметром:
        // System.setProperty("disable_default_context", "true");
        // который задан по умолчанию (true). То есть использование KeyStore в
        // дефолтном контексте отключено по умолчанию.
        //
        // То же самое касается 1ФА. Если используется вариант "System.setProperty
        // со свойствами вида javax.net.ssl.*" и при этом, естественно, ключ не
        // используется и алиас не задан, а заданы только параметры, касающиеся
        // trust store, то дефолтный контекст при включенном по умолчанию
        // disable_default_context отработает корректно. Если задать параметр
        // disable_default_context=false, т.е. отключить его, то в ходе обработки
        // параметров javax.net.ssl.* дефолтным контекстом при отсутствии алиаса
        // ключа и т.п. тип контейнера будет сформирован по умолчанию, например,
        // "HDIMAGE", а пароль - null, алиас также null, что приведет к появлению
        // окна ввода пароля для каждого найденного сертификата.
        //
        // Важно!
        // Дефолтный контекст создается TLS ревализацией один раз, а не на каждое
        // соединение.
        //
        // Важно!
        // В случае полного отказа от функций "белого" списка и использования
        // варианта "System.setProperty со свойствами вида javax.net.ssl.*"
        // рекомендуется задать disable_default_context=false:
        // System.setProperty("disable_default_context", "false");
        // Тогда поведение снова станет прежним за исключением того, что при
        // незаданном пароле (null) будет происходить запрос пароля для каждого
        // контейнера, если не задан алиас ключа.
        //
        // В случае полного отказа от функций "белого" списка и использования
        // варианта Java SSE (SSLContext, KeyManagerFactory, TrustManagerFactory)
        // с программным переопределением SSLSocketFactory у реализации TLS
        // соединения (apache http client, ok http) можно не менять параметр
        // disable_default_context.
        //

        SSLContext sslCtx;

        // Вариант №1, рекомендуемый.
        //
        // В данном случае, при клиентской аутентификации, пароль не
        // передается, он будет запрошен в окне ввода пароля.

        if (containerAdapter.isUseClientAuth()) {

            sslCtx = TLSContext.initAuthClientSSL(
                Provider.PROVIDER_NAME, // провайдер, по умолчанию - JTLS
                null,                   // протокол, по умолчанию - GostTLS
                JCSP.PROVIDER_NAME,
                keyStoreType,
                containerAdapter.getClientAlias(), // точный алиас ключа
                containerAdapter.getTrustStoreProvider(),
                containerAdapter.getTrustStoreType(),
                containerAdapter.getTrustStoreStream(),
                String.valueOf(containerAdapter.getTrustStorePassword()),
                trustManagers // для Ok Http
            );

        } // if
        else {

            sslCtx = TLSContext.initClientSSL(
                Provider.PROVIDER_NAME, // провайдер, по умолчанию - JTLS
                null,                   // протокол, по умолчанию - GostTLS
                containerAdapter.getTrustStoreProvider(),
                containerAdapter.getTrustStoreType(),
                containerAdapter.getTrustStoreStream(),
                String.valueOf(containerAdapter.getTrustStorePassword()),
                trustManagers // для Ok Http
            );

        } // else

        /*
        // Свойство disable_default_context определяет, будет ли загружен KeyStore
        // при создании дефолтного контекста реализациями типа HttpsURLConnection
        // или нет.По умолчанию оно true, т.е. загрузка KeyStore из дефолтного
        // контекста отключена.
        // Что такое дефолтный контекст? Это SSL контекст, создаваемый с параметрами
        // по умолчанию, теми, что задаются в System.setProperty (javax.net.ssl). Если
        // параметров нет, то задаются некие дефолтные значения, например, HDIMAGE
        // для типа хранилища и null для пароля. Как теперь известно, null в случае
        // пароля может привести к появлению окон ввода пароля. Поэтому загрузка
        // KeyStore в дефолтном контексте отключена.
        // Когда используется дефолтный контекст? Он может использоваться в случае
        // HttpsURLConnection, если у объекта этого типа не переопределен SSLSocketFactory.
        // Такое бывает, например, когда с помощью System.setProperty и javax.net.ssl
        // разово задают настройки для единственного TLS подключения и не используют
        // явный SSLContext. Но обычно SSLSocketFactory создается из SSLContext и
        // переопределяется у реализации намеренно, одна та все равно пытается создать
        // дефолтный контекст, хотя он и не нужен.
        //
        // Свойство disable_default_context имеет значение, только если реализация
        // TLS соединения обращается к дефолтному контексту (ей нужен/не нужен KeyStore).
        //
        // Если используется именно вариант HttpsURLConnection с System.setProperty
        // и более ничего, то рекомендуется отключить disable_default_context:
        // System.setProperty("disable_default_context", "false");
        // и задать алиас ключа в случае двухфакторной аутентификации:
        // System.setProperty("javax.net.ssl.keyStore", keyAlias);
        // чтобы при создании дефолтного контекста пароль был запрошен только
        // для keyAlias.

        // Вариант 2, прежний.

        KeyStore ts = KeyStore.getInstance(
            containerAdapter.getTrustStoreType(),
            containerAdapter.getTrustStoreProvider());

        ts.load(
            containerAdapter.getTrustStoreStream(),
            containerAdapter.getTrustStorePassword()
        );

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(
            Provider.KEYMANGER_ALG,
            Provider.PROVIDER_NAME
        );

        if (containerAdapter.isUseClientAuth()) {

            KeyStore ks = KeyStore.getInstance(
                keyStoreType,
                JCSP.PROVIDER_NAME
            );

            // Явное указание контейнера.

            if (containerAdapter.getClientAlias() != null) {
                ks.load(new StoreInputStream(containerAdapter.getClientAlias()), null);
            } // if
            else {
                ks.load(null, null);
            } // else

            kmf.init(ks, containerAdapter.getClientPassword());

        } // if

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(
            Provider.KEYMANGER_ALG,
            Provider.PROVIDER_NAME
        );

        tmf.init(ts);
        Logger.log("Create SSL context.");

        sslCtx = SSLContext.getInstance(
            Provider.ALGORITHM,
            Provider.PROVIDER_NAME
        );

        sslCtx.init(containerAdapter.isUseClientAuth()
            ? kmf.getKeyManagers() : null,
            tmf.getTrustManagers(),
            null
        );
        */
        Logger.log("SSL context completed.");
        return sslCtx;

    }

    /**
     * Вывод полученных данных.
     *
     * @param inputStream Входящий поток.
     * @throws Exception
     */
    public static void logData(InputStream inputStream)
        throws Exception {

        BufferedReader br = null;
        if (inputStream != null) {

            try {

                br = new BufferedReader(new InputStreamReader(
                    inputStream, Constants.DEFAULT_ENCODING));

                String input;
                Logger.log("*** Content begin ***");

                while ((input = br.readLine()) != null) {
                    Logger.log(input);
                } // while

                Logger.log("*** Content end ***");

            } finally {

                if (br != null) {

                    try {
                        br.close();
                    } catch (IOException e) {
                        // ignore
                    }

                }

            }

        }

    }

}

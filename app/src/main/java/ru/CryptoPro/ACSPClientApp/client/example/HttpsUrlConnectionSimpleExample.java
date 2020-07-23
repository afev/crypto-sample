/**
 * $RCSfileHttpsUrlConnectionSimpleExample.java,v $
 * version $Revision: 36379 $
 * created 09.11.2017 11:19 by afevma
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

import ru.CryptoPro.ACSPClientApp.util.ContainerAdapter;

/**
 * Класс HttpsUrlConnectionSimpleExample реализует
 * пример обмена по TLS 1.0 односторонней аутентификацией
 * с использованием HttpsUrlConnection.
 *
 * @author Copyright 2004-2017 Crypto-Pro. All rights reserved.
 * @.Version
 */
public class HttpsUrlConnectionSimpleExample extends HttpsUrlConnectionExample {

    /**
     * Конструктор.
     *
     * @param adapter Настройки примера.
     */
    public HttpsUrlConnectionSimpleExample(ContainerAdapter adapter) {
        super(adapter);
    }

}

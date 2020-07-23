/**
 * $RCSfileOkHttpSimpleExample.java,v $
 * version $Revision: 36379 $
 * created 17.02.2020 15:06 by afevma
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

import ru.CryptoPro.ACSPClientApp.util.ContainerAdapter;

/**
 * Класс OkHttpSimpleExample реализует пример обмена
 * по TLS 1.0 с помощью Ok Http v3 с односторонней
 * аутентификацией.
 *
 * @author Copyright 2004-2020 Crypto-Pro. All rights reserved.
 * @.Version
 */
public class OkHttpSimpleExample extends OkHttpExample {

    /**
     * Конструктор.
     *
     * @param adapter Настройки примера.
     */
    public OkHttpSimpleExample(ContainerAdapter adapter) {
        super(adapter);
    }

}

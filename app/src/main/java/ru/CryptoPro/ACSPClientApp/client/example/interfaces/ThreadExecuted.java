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
package ru.CryptoPro.ACSPClientApp.client.example.interfaces;

import android.util.Log;

import ru.CryptoPro.ACSPClientApp.client.example.base.FinalListener;
import ru.CryptoPro.ACSPClientApp.util.Constants;
import ru.CryptoPro.ACSPClientApp.util.Logger;

/**
 * Служебный интерфейс ThreadExecuted предназначен
 * для выполнения задачи внутри потока. Обычно это
 * задача передачи или получения данных по сети
 * интернет.
 *
 * 29/05/2013
 *
 */
public abstract class ThreadExecuted {

    /**
     * Обработчик события.
     */
    protected FinalListener finalListener = null;

    /**
     * Добавление обработчика события.
     *
     * @param listener Обработчик события.
     */
    public void addFinalListener(FinalListener listener) {
        finalListener = listener;
    }

    /**
     * Метод для выполнения задачи в потоке.
     * Задача записывается внутри метода.
     *
     * @throws Exception
     */
    protected abstract void executeOne() throws Exception;

    /**
     * Метод для выполнения задачи в потоке.
     * Задача записана внутри метода.
     *
     */
    public void execute() {

        try {

            executeOne();
            Logger.setStatusOK();

            if (finalListener != null) {
                finalListener.onComplete();
            } // if

        } catch (Exception e) {

            Logger.log(e.getMessage());
            Logger.setStatusFailed();

            Log.e(Constants.APP_LOGGER_TAG, "Operation exception", e);

        }

    }

}

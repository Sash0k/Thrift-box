package ru.sash0k.thriftbox;

import android.util.Log;

/**
 * Вспомогательные методы
 */
public class Utils {
    public static final String TAG = "THRIFTBOX";

    public static final boolean DEBUG = true;

    public static void log(String msg) {
        if (DEBUG) Log.w(TAG, msg);
    }

    /**
     * Проверка введённых значений сумм.
     * Возвращает введённое значение в копейках или -1 при некорректном вводе
     */
    public static int parseCurrency(String value) {
        if (value.matches("\\d+(\\.\\d{1,2})?")) {
            final int dot = value.indexOf(".");
            if (dot == -1) return Integer.parseInt(value) * 100; // дробной части нет
            else {
                int rub = Integer.parseInt(value.substring(0, dot)); // целая часть
                // разбор копеек
                value = value.substring(dot + 1);
                if (value.length() == 1) value += "0";
                int cop = Integer.parseInt(value);
                return rub * 100 + cop;
            }
        } else return -1;
    }
    // ============================================================================

}

package ru.sash0k.thriftbox;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

/**
 * Вспомогательные методы
 */
public class Utils {
    public static final String TAG = "THRIFTBOX";

    public static final char ROUBLE = '\u20BD';
    public static final String ROUBLE_FONT = "fonts/rouble2.ttf";
    public static void log(String msg) {
        if (BuildConfig.DEBUG) Log.w(TAG, msg);
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

    /**
     * Отображение суммы в читаемом виде
     */
    public static String formatValue(long value) {
        return value / 100 + "." + String.format("%02d", value % 100);
    }
    // ============================================================================

    /**
     * Получение временных меток начала суток, недели, месяца
     */
    public static long[] getTimestamps() {
        final int MILLIS = 1000;
        long[] result = new long[3];
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        result[0] = c.getTimeInMillis() / MILLIS;
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        result[1] = c.getTimeInMillis() / MILLIS;

        Calendar mc = Calendar.getInstance();
        mc.set(Calendar.DAY_OF_MONTH, 1);
        mc.set(Calendar.HOUR_OF_DAY, 0);
        mc.set(Calendar.MINUTE, 0);
        mc.set(Calendar.SECOND, 0);
        result[2] = mc.getTimeInMillis() / MILLIS;
        return result;
    }
    // ============================================================================

    /**
     * Обновить виджеты
     */
    public static void updateWidgets(Context context) {
        Intent intent = new Intent(context, Widget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, Widget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
    // ============================================================================

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void setLayerTypeCompat(View view, int layerType) {
        if (hasHoneycomb()) {
            view.setLayerType(layerType, null);
        }
    }

    public static void setBackgroundCompat(View view, Drawable drawable) {
        if (hasJellyBean()) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

}

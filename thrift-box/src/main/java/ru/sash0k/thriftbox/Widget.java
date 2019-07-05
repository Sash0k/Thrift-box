package ru.sash0k.thriftbox;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.Calendar;

import ru.sash0k.thriftbox.database.DB;
import ru.sash0k.thriftbox.fragments.SettingsFragment;

/**
 * Реализация виджета для оторбражения расходов
 * Created by sash0k on 15.04.14.
 */
public class Widget extends AppWidgetProvider {
    public static final String ACTION_AUTO_UPDATE_WIDGET = "ACTION_AUTO_UPDATE_WIDGET";
    public static final String alpha = Utils.hasJellyBean() ? "setImageAlpha" : "setAlpha";
    private static final char ROUBLE = Utils.hasLollipop() ? Utils.ROUBLE : 'р';


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(Widget.ACTION_AUTO_UPDATE_WIDGET);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 1);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);  // TODO: Android 4.4
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        Intent intent = new Intent(Widget.ACTION_AUTO_UPDATE_WIDGET);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(PendingIntent.getBroadcast(context, 0, intent, 0));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_AUTO_UPDATE_WIDGET.equals(intent.getAction())) {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            int[] widgets = widgetManager.getAppWidgetIds(new ComponentName(context, Widget.class));
            for (int widget : widgets) updateWidget(context, widgetManager, widget);
        }
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetID) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget);
        long[] timestamps = Utils.getTimestamps();
        final String today = Utils.formatValue(DB.getExpense(context, timestamps[0]));
        final String week = Utils.formatValue(DB.getExpense(context, timestamps[1]));
        final String month = Utils.formatValue(DB.getExpense(context, timestamps[2]));

        // настройка прозрачности виджета
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final int transparency = Integer.parseInt(preferences.getString(
                SettingsFragment.PREF_WIDGET_TRANSPARENCY_KEY, "255"));
        widgetView.setInt(R.id.widget_background, "setColorFilter", Color.WHITE);
        widgetView.setInt(R.id.widget_background, alpha, transparency);

        // настройка цвета текста
        final int color = preferences.getInt(SettingsFragment.PREF_WIDGET_TEXT_COLOR_KEY, Color.BLACK);
        widgetView.setImageViewBitmap(R.id.ruble_icon, changeBitmapColor(context, R.mipmap.ic_currency_rub_black_24dp, color));
        widgetView.setTextColor(R.id.widget_today, color);
        widgetView.setTextColor(R.id.widget_week, color);
        widgetView.setTextColor(R.id.widget_month, color);

        widgetView.setTextViewText(R.id.widget_today, today);
        widgetView.setTextViewText(R.id.widget_week, context.getString(R.string.week) + " " + week + " " + ROUBLE);
        widgetView.setTextViewText(R.id.widget_month, context.getString(R.string.month) + " " + month + " " + ROUBLE);

        // Запуск по клику на виджет
        final Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        widgetView.setOnClickPendingIntent(R.id.widget, pendingIntent);

        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }

    private static Bitmap changeBitmapColor(Context context, int resId, int color) {
        BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(resId);
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        Bitmap bmp = drawable.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Canvas myCanvas = new Canvas(bmp);
        int myColor = bmp.getPixel(0, 0);
        ColorFilter filter = new LightingColorFilter(myColor, color);
        Paint pnt = new Paint();
        pnt.setColorFilter(filter);
        myCanvas.drawBitmap(bmp, 0, 0, pnt);
        return bmp;
    }
}
package ru.sash0k.thriftbox;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import ru.sash0k.thriftbox.database.DB;

/**
 * Реализация виджета для оторбражения расходов
 * Created by sash0k on 15.04.14.
 */
public class Widget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetID) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget);
        long[] timestamps = Utils.getTimestamps();
        final String today = Utils.formatValue(DB.getExpense(context, timestamps[0]));
        final String week = Utils.formatValue(DB.getExpense(context, timestamps[1]));
        final String month = Utils.formatValue(DB.getExpense(context, timestamps[2]));

        widgetView.setTextViewText(R.id.widget_today, today);
        widgetView.setTextViewText(R.id.widget_week, context.getString(R.string.week) + " " + week + " " + context.getString(R.string.ruble));
        widgetView.setTextViewText(R.id.widget_month, context.getString(R.string.month) + " " + month + " " + context.getString(R.string.ruble));

        // Запуск по клику на виджет
        final Intent intent = new Intent(context, InputActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        widgetView.setOnClickPendingIntent(R.id.widget, pendingIntent);

        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }
}

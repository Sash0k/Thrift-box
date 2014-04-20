package ru.sash0k.thriftbox;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.Arrays;

import ru.sash0k.thriftbox.database.DB;

/**
 * Реализация виджета для оторбражения расходов
 * Created by sash0k on 15.04.14.
 */
public class Widget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Utils.log("widget onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Utils.log("widget onUpdate " + Arrays.toString(appWidgetIds));
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Utils.log("widget onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Utils.log("widget onDisabled");
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetID) {
        Utils.log("widget updateWidget " + widgetID);

        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget);
        widgetView.setTextViewText(R.id.widget_today, DB.getTodayExpense(context));
        widgetView.setTextViewText(R.id.widget_week, "Неделя: 0.00 р");
        widgetView.setTextViewText(R.id.widget_month, "Месяц: 0.00 р");

//        // Запуск сервиса по клику на кнопку виджета
//        final Intent intent = new Intent(context, OpenService.class);
//        intent.putExtra(NAME, name);
//        intent.putExtra(AUTH, auth);
//        intent.putExtra(MAC, mac);
//        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
//        widgetView.setOnClickPendingIntent(R.id.widget_button, pendingIntent);

        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }
}

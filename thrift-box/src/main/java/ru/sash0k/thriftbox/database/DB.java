package ru.sash0k.thriftbox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created with IntelliJ IDEA.
 * User: sash0k
 */
public class DB {

    /**
     * Created with IntelliJ IDEA.
     * User: sash0k
     */
    public static class DbOpenHelper extends SQLiteAssetHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "thriftbox.db";

        public DbOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }
    }
    // ====================================================================

    // рабочие таблицы
    public static final String EXPENSES_TABLE = "expenses";
    public static final String EXPENSES_VIEW = "expenses_view";

    // поля таблицы EXPENSES_TABLE
    // BaseColumns._ID
    public static final String VALUE = "value";
    public static final String CATEGORY = "category";
    public static final String TIMESTAMP = "timestamp";

    // поля таблицы EXPENSES_VIEW
    public static final String DATE = "date";
    // ====================================================================

    /**
     * Получить URI для работы с провайдером
     *
     * @param table - нужная таблица
     * @return
     */
    public static Uri getUri(String table) {
        return Uri.parse("content://" + DBProvider.AUTHORITY + "/" + table);
    }
    // ====================================================================

    /**
     * Удалить таблицу
     */
    public static void clean(Context context, String table) {
        context.getContentResolver().delete(getUri(table), null, null);
    }
    // ====================================================================

    /**
     * Добавить запись в таблицу
     */
    public static void insertItem(Context context, int value, int category) {
        ContentValues values = new ContentValues();
        values.put(VALUE, value);
        values.put(CATEGORY, category);
        context.getContentResolver().insert(getUri(EXPENSES_VIEW), values);
    }
    // ====================================================================

    /**
     * Удалить запись из таблицы
     */
    public static void deleteItem(Context context, int id) {
        // чтобы не писать триггер удаления EXPENSES_VIEW
        context.getContentResolver().delete(getUri(EXPENSES_TABLE), BaseColumns._ID + "=?", new String[]{Integer.toString(id)});
        context.getContentResolver().notifyChange(getUri(EXPENSES_VIEW), null);
    }
    // ====================================================================

    /**
     * Получить расход за сегодняшний день
     */
    public static long getExpense(Context context, long timestamp) {
        final String[] columns = new String[]{"SUM(" + VALUE + ")"};
        final String where = TIMESTAMP + ">=?";
        Cursor mCursor = context.getContentResolver().query(getUri(EXPENSES_TABLE),
                columns, where, new String[]{Long.toString(timestamp)}, null);
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                final long result = mCursor.getLong(0);
                mCursor.close();
                return result;
            } else mCursor.close();
        }
        return 0;
    }
    // ====================================================================

}
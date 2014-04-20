package ru.sash0k.thriftbox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Calendar;

import ru.sash0k.thriftbox.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: sash0k
 */
public class DB {

    /**
     * Created with IntelliJ IDEA.
     * User: sash0k
     */
    public static class DbOpenHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "thriftbox.db";

        public DbOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            final String expenses_table = "CREATE TABLE [" + EXPENSES_TABLE + "]" +
                    " ([" + BaseColumns._ID + "] INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " [" + VALUE + "] INTEGER NOT NULL," +
                    " [" + CATEGORY + "] INTEGER NOT NULL DEFAULT(0)," +
                    " [" + TIMESTAMP + "] INTEGER NOT NULL DEFAULT(strftime('%s','now')));";
            sqLiteDatabase.execSQL(expenses_table);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EXPENSES_TABLE);
            onCreate(sqLiteDatabase);
        }
    }
    // ====================================================================

    // рабочие таблицы
    public static final String EXPENSES_TABLE = "expenses";

    // поля таблицы EXPENSES_TABLE
    // BaseColumns._ID
    public static final String VALUE = "value";
    public static final String CATEGORY = "category";
    public static final String TIMESTAMP = "timestamp";
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
        context.getContentResolver().insert(getUri(EXPENSES_TABLE), values);
    }
    // ====================================================================

    /**
     * Получить расход за сегодняшний день
     */
    public static String getTodayExpense(Context context) {
        final long t0 = System.currentTimeMillis();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        final String timestamp = Long.toString(c.getTimeInMillis() / 1000);

        final String[] columns = new String[] { "sum(" + VALUE + ")" };
        final String where = TIMESTAMP + ">?";
        Cursor mCursor = context.getContentResolver().query(getUri(EXPENSES_TABLE), columns, where, new String[]{timestamp}, null);
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                final long today = mCursor.getLong(0);
                final String result = today / 100 + "." + today % 100;
                mCursor.close();
                Utils.log("getTodayExpense = " + result + ", got from DB for " + (System.currentTimeMillis() - t0) + " ms");
                return result;
            } else mCursor.close();
        }
        return null;
    }
    // ====================================================================

}
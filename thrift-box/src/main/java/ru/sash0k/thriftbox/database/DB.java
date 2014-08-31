package ru.sash0k.thriftbox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

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
        private static final int DB_VERSION = 2;
        private static final String DB_NAME = "thriftbox.db";

        public DbOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            final String expenses_table = "CREATE TABLE IF NOT EXISTS [" + EXPENSES_TABLE + "]" +
                    " ([" + BaseColumns._ID + "] INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " [" + VALUE + "] INTEGER NOT NULL," +
                    " [" + CATEGORY + "] INTEGER NOT NULL DEFAULT(0)," +
                    " [" + COMMENT + "] TEXT," +
                    " [" + TIMESTAMP + "] INTEGER NOT NULL DEFAULT(strftime('%s','now')));";

            final String expenses_view = "CREATE VIEW IF NOT EXISTS [" + EXPENSES_VIEW + "] AS SELECT " +
                    BaseColumns._ID + ", " + VALUE + ", " + CATEGORY + ", "
                    + COMMENT + ", " + TIMESTAMP + ", " +
                    "strftime('%d.%m.%Y', " + TIMESTAMP + ", 'unixepoch', 'localtime') AS " + DATE +
                    " FROM " + EXPENSES_TABLE;

            final String insert_trigger = "CREATE TRIGGER IF NOT EXISTS " +
                    "insert_expenses instead of insert on " + EXPENSES_VIEW +
                    " BEGIN INSERT into " + EXPENSES_TABLE + "(" + VALUE + ", " + CATEGORY + ", " + COMMENT + ")" +
                    " values(new." + VALUE + ", new." + CATEGORY + ", new." + COMMENT + "); END;";

            final String delete_trigger = "CREATE TRIGGER IF NOT EXISTS " +
                    "delete_expenses instead of delete on " + EXPENSES_VIEW +
                    " BEGIN DELETE from " + EXPENSES_TABLE + " WHERE " + BaseColumns._ID + " = OLD." + BaseColumns._ID + "; END;";

            sqLiteDatabase.execSQL(expenses_table);
            sqLiteDatabase.execSQL(expenses_view);
            sqLiteDatabase.execSQL(insert_trigger);
            sqLiteDatabase.execSQL(delete_trigger);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            Utils.log("db onUpgrade: " + oldVersion + " >> " + newVersion);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS insert_expenses");
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS delete_expenses");
            sqLiteDatabase.execSQL("DROP VIEW IF EXISTS " + EXPENSES_VIEW);
            if (oldVersion == 1 && newVersion == 2) {
                // добавил категорию "Дети", смена номера категории "Путешествия" с 9 на 11
                sqLiteDatabase.execSQL("UPDATE " + EXPENSES_TABLE + " SET " + CATEGORY + " = 11 WHERE " + CATEGORY + " = 9" );
                sqLiteDatabase.execSQL("ALTER TABLE " + EXPENSES_TABLE + " ADD COLUMN " + COMMENT + " TEXT");
            }
            else sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EXPENSES_TABLE);
            onCreate(sqLiteDatabase);
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
    public static final String COMMENT = "comment";
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
        context.getContentResolver().delete(getUri(EXPENSES_VIEW), BaseColumns._ID + "=?", new String[]{Integer.toString(id)});
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
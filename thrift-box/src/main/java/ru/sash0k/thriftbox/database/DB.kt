package ru.sash0k.thriftbox.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.BaseColumns
import ru.sash0k.thriftbox.Utils
import java.util.ArrayList

class DB(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "thriftbox.db"
        const val DB_VERSION = 3

        // рабочие таблицы|вьюхи
        const val EXPENSES_TABLE = "expenses"
        const val EXPENSES_VIEW = "expenses_view"
        const val STATISTICS_VIEW = "statistics_view"

        // поля
        const val VALUE = "value"
        const val CATEGORY = "category"
        const val COMMENT = "comment"
        const val TIMESTAMP = "timestamp"
        const val DATE = "date"

        @JvmStatic
        fun getUri(table: String): Uri {
            return Uri.parse("content://" + DBProvider.AUTHORITY + "/" + table)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val doExpensesTable = ("CREATE TABLE IF NOT EXISTS $EXPENSES_TABLE" +
                " ([" + BaseColumns._ID + "] INTEGER PRIMARY KEY AUTOINCREMENT," +
                " [$VALUE] INTEGER NOT NULL," +
                " [$CATEGORY] INTEGER NOT NULL DEFAULT(0)," +
                " [$COMMENT] TEXT," +
                " [$TIMESTAMP] INTEGER NOT NULL DEFAULT(strftime('%s','now')));")

        val doExpensesView = ("CREATE VIEW IF NOT EXISTS $EXPENSES_VIEW AS SELECT " +
                BaseColumns._ID + ", $VALUE, $CATEGORY, $COMMENT, $TIMESTAMP," +
                " strftime('%d.%m.%Y', $TIMESTAMP, 'unixepoch', 'localtime') AS $DATE" +
                " FROM $EXPENSES_TABLE")

        val doStatisticsView = ("CREATE VIEW IF NOT EXISTS $STATISTICS_VIEW AS SELECT " +
                "(sum($VALUE)*1.0/100) AS $VALUE, $CATEGORY, $TIMESTAMP"
                + " FROM (SELECT $VALUE, $CATEGORY,"
                + " strftime('%s', date($TIMESTAMP, 'unixepoch', 'start of month')) AS $TIMESTAMP"
                + " FROM $EXPENSES_TABLE)"
                + " GROUP BY $TIMESTAMP, $CATEGORY")

        val doInsertTrigger = ("CREATE TRIGGER IF NOT EXISTS insert_expenses" +
                " instead of insert on $EXPENSES_VIEW" +
                " BEGIN INSERT into $EXPENSES_TABLE ($VALUE, $CATEGORY, $COMMENT)" +
                " values(new.$VALUE, new.$CATEGORY, new.$COMMENT);" +
                " END;")

        val doDeleteTrigger = ("CREATE TRIGGER IF NOT EXISTS delete_expenses" +
                " instead of delete on $EXPENSES_VIEW" +
                " BEGIN DELETE from $EXPENSES_TABLE WHERE " + BaseColumns._ID + " = OLD." + BaseColumns._ID + ";" +
                " END;")

        db.execSQL(doExpensesTable)
        db.execSQL(doExpensesView)
        db.execSQL(doStatisticsView)
        db.execSQL(doInsertTrigger)
        db.execSQL(doDeleteTrigger)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Utils.log("db onUpgrade: $oldVersion >> $newVersion")

        db.execSQL("DROP TRIGGER IF EXISTS insert_expenses")
        db.execSQL("DROP TRIGGER IF EXISTS delete_expenses")
        db.execSQL("DROP VIEW IF EXISTS $EXPENSES_VIEW")
        db.execSQL("DROP VIEW IF EXISTS $STATISTICS_VIEW")

        if (oldVersion == 1 && newVersion == 2) {
            // добавил категорию "Дети", смена номера категории "Путешествия" с 9 на 11
            db.execSQL("UPDATE $EXPENSES_TABLE SET $CATEGORY = 11 WHERE $CATEGORY = 9")
            db.execSQL("ALTER TABLE $EXPENSES_TABLE ADD COLUMN $COMMENT TEXT")
        }
        if (oldVersion == 2 && newVersion == 3) {
            // создаётся STATISTICS_VIEW, удалять рабочую таблицу не нужно
        } else
            db.execSQL("DROP TABLE IF EXISTS $EXPENSES_TABLE")

        onCreate(db)
    }

    /**
     * Добавить запись в таблицу
     */
    fun insertItem(context: Context, value: Int, category: Int, comment: String?) {
        val values = ContentValues()
        values.put(VALUE, value)
        values.put(CATEGORY, category)
        if (!comment.isNullOrBlank()) values.put(COMMENT, comment)

        context.contentResolver.insert(getUri(EXPENSES_VIEW), values)
    }

    /**
     * Удалить запись из таблицы
     */
    fun deleteItem(context: Context, id: Int) {
        context.contentResolver.delete(getUri(EXPENSES_VIEW), BaseColumns._ID + "=?", arrayOf(Integer.toString(id)))
    }

    /**
     * Получить расход за сегодняшний день
     */
    fun getExpense(context: Context, timestamp: Long): Long {
        val mCursor = context.contentResolver.query(getUri(EXPENSES_TABLE)
                , arrayOf("SUM($VALUE)")
                , "$TIMESTAMP>=?"
                , arrayOf(java.lang.Long.toString(timestamp))
                , null)

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                val result = mCursor.getLong(0)
                mCursor.close()
                return result
            } else
                mCursor.close()
        }
        return 0
    }

    /**
     * Получение статистики по категориям
     * @param timestamp - интересующий месяц
     */
    fun getStatData(context: Context, timestamp: Long, count: Int): List<Float> {
        val result = ArrayList<Float>(count)
        for (i in 0 until count) result.add(0.0f)

        val mCursor = context.contentResolver.query(getUri(STATISTICS_VIEW)
                , arrayOf(CATEGORY, VALUE)
                , "$TIMESTAMP=?"
                , arrayOf(java.lang.Long.toString(timestamp))
                , CATEGORY)
                ?: return result

        while (mCursor.moveToNext()) {
            result[mCursor.getInt(0)] = mCursor.getFloat(1)
        }
        mCursor.close()
        return result
    }
}
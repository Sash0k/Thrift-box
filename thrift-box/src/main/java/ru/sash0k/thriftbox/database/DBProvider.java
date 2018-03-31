package ru.sash0k.thriftbox.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import ru.sash0k.thriftbox.BuildConfig;

/**
 * Created with IntelliJ IDEA.
 * User: sash0k
 * Провайдер для соединения с локальной базой данных
 */
public class DBProvider extends ContentProvider {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".database.DBProvider";

    private DB databaseHandler;
    private SQLiteDatabase database;

    // Таблицы
    private static final UriMatcher uriMatcher;
    public static final int URI_EXPENSES = 0;
    public static final int URI_EXPENSES_VIEW = 1;
    public static final int URI_STATISTICS_VIEW = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, DB.EXPENSES_TABLE, URI_EXPENSES);
        uriMatcher.addURI(AUTHORITY, DB.EXPENSES_VIEW, URI_EXPENSES_VIEW);
        uriMatcher.addURI(AUTHORITY, DB.STATISTICS_VIEW, URI_STATISTICS_VIEW);
    }

    public static int getMode(String table) {
        int mode = URI_EXPENSES;
        if (table.equals(DB.EXPENSES_TABLE)) mode = DBProvider.URI_EXPENSES;
        else if (table.equals(DB.EXPENSES_VIEW)) mode = DBProvider.URI_EXPENSES_VIEW;
        return mode;
    }

    private static String selectTable(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_EXPENSES:
                return DB.EXPENSES_TABLE;
            case URI_EXPENSES_VIEW:
                return DB.EXPENSES_VIEW;
            case URI_STATISTICS_VIEW:
                return DB.STATISTICS_VIEW;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        databaseHandler = new DB(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return simpleQuery(selectTable(uri), projection, selection, selectionArgs, sortOrder);
    }

    private Cursor simpleQuery(String table, String[] projection, String selection,
                               String[] selectionArgs, String sortOrder) {
        Uri uriMain = Uri.parse("content://" + AUTHORITY + "/" + table);
        database = databaseHandler.getWritableDatabase();
        Cursor cursor = database.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uriMain);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final String table = selectTable(uri);
        Uri uriMain = Uri.parse("content://" + AUTHORITY + "/" + table);
        database = databaseHandler.getWritableDatabase();
        long rowID = database.insert(table, null, contentValues);

        Uri resultUri = ContentUris.withAppendedId(uriMain, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        final String table = selectTable(uri);
        database = databaseHandler.getWritableDatabase();
        database.beginTransaction();
        try {
            for (ContentValues cv : values) {
                final long status = database.insertOrThrow(table, null, cv);
                //Utils.log("insert status = " + status);
            }
            database.setTransactionSuccessful();
            numInserted = values.length;
        } catch (SQLException e) {
            e.printStackTrace();
            //Utils.log("insert error: " + e.getMessage());
        } finally {
            database.endTransaction();
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numInserted;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final String table = selectTable(uri);
        database = databaseHandler.getWritableDatabase();
        int cnt = database.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final String table = selectTable(uri);
        database = databaseHandler.getWritableDatabase();
        int cnt = database.update(table, contentValues, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        final String table = selectTable(uri);
        return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + table;
    }
}

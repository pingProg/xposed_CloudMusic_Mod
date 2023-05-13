package com.ping.cloudmusicmod;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DataContentProvider extends ContentProvider {
    private static final int DATA_KEY_VALUE = 1;
    private static final int DATA_KEY_VALUE_ID = 2;

    private DbHelper dbHelper;
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());

        // 设置 URI 匹配器
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_DATA, DATA_KEY_VALUE);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_DATA + "/#", DATA_KEY_VALUE_ID);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {
            case DATA_KEY_VALUE:
                cursor = db.query(DataContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DATA_KEY_VALUE_ID:
                selection = DataContract.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(DataContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
   }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case DATA_KEY_VALUE:
                long id = db.insert(DataContract.TABLE_NAME, null, values);
                if (id == -1) {
                    Log.e("DataContentProvider", "Failed to insert row for " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case DATA_KEY_VALUE:
                int rowsUpdated = db.update(DataContract.TABLE_NAME, values, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
            case DATA_KEY_VALUE_ID:
                selection = DataContract.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowUpdated = db.update(DataContract.TABLE_NAME, values, selection, selectionArgs);
                if (rowUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowUpdated;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case DATA_KEY_VALUE:
                int rowsDeleted = db.delete(DataContract.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case DATA_KEY_VALUE_ID:
                selection = DataContract.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowDeleted = db.delete(DataContract.TABLE_NAME, selection, selectionArgs);
                if (rowDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowDeleted;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case DATA_KEY_VALUE:
                return DataContract.CONTENT_LIST_TYPE;
            case DATA_KEY_VALUE_ID:
                return DataContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}

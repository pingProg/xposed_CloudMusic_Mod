package com.ping.cloudmusicmod;

import static com.ping.cloudmusicmod.utils.CommonUtils.LogDebug;
import static com.ping.cloudmusicmod.utils.CommonUtils.LogError;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DataDB {
    public final static String DEFAULT_ERROR_STRING = "";
    public final static Uri uri = DataDBContract.CONTENT_URI;

    public static void debugPrintAll() {
        Cursor cursor = getContext().getContentResolver().query(uri,
                null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DataDBContract.COLUMN_ID));
                @SuppressLint("Range") String key = cursor.getString(cursor.getColumnIndex(DataDBContract.COLUMN_KEY));
                @SuppressLint("Range") String value = cursor.getString(cursor.getColumnIndex(DataDBContract.COLUMN_VALUE));

                // 处理查询结果
                LogDebug(String.format("ID : %d, KEY : %s, VALUE : %s", id, key, value));
            }
            cursor.close();
        } else {
            LogError(String.format("!!!! error"), null);
        }
    }

    public static boolean isKeyExist(String key) {
        String[] projection = {DataDBContract.COLUMN_ID};
        String selection = DataDBContract.COLUMN_KEY + " = ?";
        String[] selectionArgs = {key};

        Cursor cursor = getContext().getContentResolver().query(
                uri, projection, selection, selectionArgs, null);

        boolean ret = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return ret;
    }

    public static String queryValue(String key) {
        String[] projection = {DataDBContract.COLUMN_VALUE};
        String selection = DataDBContract.COLUMN_KEY + " = ?";
        String[] selectionArgs = {key};

        Cursor cursor = getContext().getContentResolver().query(
                uri, projection, selection, selectionArgs, null);

        String ret;
        if (cursor != null && cursor.moveToFirst()) {
            int valueIndex = cursor.getColumnIndex(DataDBContract.COLUMN_VALUE);
            ret = cursor.getString(valueIndex);
        } else {
            ret = DEFAULT_ERROR_STRING;
        }
        if (cursor != null) {
            cursor.close();
        }
        return ret;
    }

    private static boolean update(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(DataDBContract.COLUMN_VALUE, value);

        String selection = DataDBContract.COLUMN_KEY + " = ?";
        String[] selectionArgs = {key};
        int rowsUpdated = getContext().getContentResolver().update(
                uri, values, selection, selectionArgs);

        return rowsUpdated == 1;
    }

    private static Uri insert(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(DataDBContract.COLUMN_KEY, key);
        values.put(DataDBContract.COLUMN_VALUE, value);
        // 数据不存在，插入新数据
        return getContext().getContentResolver().insert(uri, values);
    }

    public static Uri insertOrUpdate(String key, String value) {
        // 查询指定的数据是否存在
        if (isKeyExist(key)) {
            // 数据存在，更新数据
            ContentValues values = new ContentValues();
            values.put(key, value);
            boolean ret = update(key, value);
            if (ret) {
                LogDebug(String.format("UPDATE : %s %s", key, value));
            } else {
                LogError("ERROR : 更新了多个键值对，可能发生了错误！", null);
            }
            return uri;
        } else {
            // 数据不存在，插入新数据
            Uri newUri = insert(key, value);
            LogDebug(String.format("INSERT : %s %s", key, value));
            return newUri;
        }
    }

    private static Context getContext() {
        return (Context) AndroidAppHelper.currentApplication();
    }
}

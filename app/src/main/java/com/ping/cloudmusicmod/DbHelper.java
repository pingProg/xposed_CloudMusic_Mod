package com.ping.cloudmusicmod;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CloudMusicMod.db";
    private static final int DATABASE_VERSION = 2;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表
        String createTableQuery = "CREATE TABLE " + DataDBContract.TABLE_NAME + " (" +
                DataDBContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataDBContract.COLUMN_KEY + " TEXT UNIQUE, " +
                DataDBContract.COLUMN_VALUE + " TEXT)";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 处理数据库升级逻辑
        // 在这个示例中，我们简单地删除旧表并重新创建新表
        db.execSQL("DROP TABLE IF EXISTS " + DataDBContract.TABLE_NAME);
        onCreate(db);
    }
}

package com.luiz.mg.magickey.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

 public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +

                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +

                    FeedReaderContract.FeedEntry.COLUMN_NAME_KEY + " TEXT," +

                    FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_TAKE + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_TAKE + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_TAKE + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_TAKE + " TEXT," +

                    FeedReaderContract.FeedEntry.COLUMN_NAME_MAT_BACK + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NAME_BACK + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATE_BACK + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TIME_BACK + " TEXT)";

    private static final String SQL_CREATE_KEYS =
            "CREATE TABLE " + FeedReaderContract.Feedkeys.TABLE_NAME + " (" +
                    FeedReaderContract.Feedkeys._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.Feedkeys.COLUMN_NAME_NAME + " TEXT," +
                    FeedReaderContract.Feedkeys.COLUMN_NAME_DEPT + " TEXT," +
                    FeedReaderContract.Feedkeys.COLUMN_NAME_BORROWED + " TEXT)";

    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + FeedReaderContract.FeedUsers.TABLE_NAME + " (" +
                    FeedReaderContract.FeedUsers._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedUsers.COLUMN_NAME_MAT + " TEXT," +
                    FeedReaderContract.FeedUsers.COLUMN_NAME_NAME + " TEXT," +
                    FeedReaderContract.FeedUsers.COLUMN_NAME_DEPT + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    private static final String SQL_DELETE_KEYS =
            "DROP TABLE IF EXISTS " + FeedReaderContract.Feedkeys.TABLE_NAME;

    private static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedUsers.TABLE_NAME;

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_KEYS);
        db.execSQL(SQL_CREATE_USERS);
        Log.d("appkey", "Chamou onCreate DB");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_KEYS);
        db.execSQL(SQL_DELETE_USERS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

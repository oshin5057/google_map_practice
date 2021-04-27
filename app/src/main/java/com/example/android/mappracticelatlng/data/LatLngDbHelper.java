package com.example.android.mappracticelatlng.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LatLngDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "latlngs.db";
    public static final int DATABASE_VERSION = 1;

    public LatLngDbHelper(Context context){
        super(context, TABLE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_LATLNG_TABLE = "CREATE TABLE " + LatLngContract.LatLngEntry.TABLE_NAME + " ("
                + LatLngContract.LatLngEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LatLngContract.LatLngEntry.COLUMN_LATITUDE + " TEXT NOT NULL, "
                + LatLngContract.LatLngEntry.COLUMN_LONGITUDE + " TEXT NOT NULL, "
                + LatLngContract.LatLngEntry.COLUMN_ADDRESS + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_LATLNG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}

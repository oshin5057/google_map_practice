package com.example.android.mappracticelatlng.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LatLngProvider extends ContentProvider {

    public static final String TAG = LatLngProvider.class.getSimpleName();

    private LatLngDbHelper mDbHelper;

    private static final int LATLNGS = 100;

    private static final int LATLNGS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(LatLngContract.CONTENT_AUTHORITY, LatLngContract.PATH_LAT_LNG, LATLNGS);
        sUriMatcher.addURI(LatLngContract.CONTENT_AUTHORITY, LatLngContract.PATH_LAT_LNG + "/#", LATLNGS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new LatLngDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case LATLNGS:
                cursor = database.query(LatLngContract.LatLngEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case LATLNGS_ID:
                selection = LatLngContract.LatLngEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(LatLngContract.LatLngEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query UNKNOWN Uri " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        if (match == LATLNGS) {
            return insertLatLng(uri, contentValues);
        }
        throw new IllegalArgumentException("Insertion is not supported" + uri);
    }

    private Uri insertLatLng(Uri uri, ContentValues values){
        String latitude = values.getAsString(LatLngContract.LatLngEntry.COLUMN_LATITUDE);
        if (latitude == null){
            throw new IllegalArgumentException("Not inserted latitude : ");
        }

        String longitude = values.getAsString(LatLngContract.LatLngEntry.COLUMN_LONGITUDE);
        if (longitude == null){
            throw new IllegalArgumentException("Not inserted longitude : ");
        }

        String address = values.getAsString(LatLngContract.LatLngEntry.COLUMN_ADDRESS);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(LatLngContract.LatLngEntry.TABLE_NAME, null, values);

        if (id == -1){
            Log.e(TAG, "Failed to insert row " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowDeleted;

        final int match = sUriMatcher.match(uri);

        switch (match){
            case LATLNGS:
                rowDeleted = database.delete(LatLngContract.LatLngEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case LATLNGS_ID:
                selection = LatLngContract.LatLngEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = database.delete(LatLngContract.LatLngEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case LATLNGS:
                return LatLngContract.LatLngEntry.CONTENT_LIST_TYPE;

            case LATLNGS_ID:
                return LatLngContract.LatLngEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown Uri " + uri+ " with matches " + match);
        }
    }

}

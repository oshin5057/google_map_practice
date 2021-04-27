package com.example.android.mappracticelatlng.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class LatLngContract {

    private LatLngContract(){
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.mappracticelatlng";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LAT_LNG = "latlngs";

    public static final class LatLngEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LAT_LNG);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LAT_LNG;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LAT_LNG;

        public static final String TABLE_NAME = "latlngs";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_LATITUDE = "latitude";

        public static final String COLUMN_LONGITUDE = "longitude";

        public static final String COLUMN_ADDRESS = "address";

    }

}

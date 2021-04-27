package com.example.android.mappracticelatlng;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.android.mappracticelatlng.data.LatLngContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {


    public static final String TAG = ("MapActivity: ");

    GoogleMap mMap;

    private Uri mUri;

    ArrayList<LatLng> latLngArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) {

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            fm.getMapAsync(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        mUri = LatLngContract.LatLngEntry.CONTENT_URI;

        String[] projection ={
                LatLngContract.LatLngEntry._ID,
                LatLngContract.LatLngEntry.COLUMN_LATITUDE,
                LatLngContract.LatLngEntry.COLUMN_LONGITUDE,
                LatLngContract.LatLngEntry.COLUMN_ADDRESS
        };

        return new CursorLoader(MapActivity.this,
                mUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        Log.d(TAG, "onLoadFinished: called" );

        double lat =0;
        double lng =0;

        int locationCount = cursor.getCount();
        cursor.moveToFirst();
        for (int i = 0; i<locationCount; i++){
            double latitudeIndex = cursor.getColumnIndex(LatLngContract.LatLngEntry.COLUMN_LATITUDE);
            double longitudeIndex = cursor.getColumnIndex(LatLngContract.LatLngEntry.COLUMN_LONGITUDE);

            int addressIndex = cursor.getColumnIndex(LatLngContract.LatLngEntry.COLUMN_ADDRESS);

            String[] latitude = cursor.getString((int) latitudeIndex).split(":");
            String[] longitude = cursor.getString((int) longitudeIndex).split(":");

            String address = cursor.getString(addressIndex);
            Log.d(TAG, "Address: " + address);

            lat = Double.parseDouble(latitude[1].trim());
            lng = Double.parseDouble(longitude[1].trim());

            Log.d(TAG, "Latitude: " + lat + "  Longitude: " + lng);


            LatLng locationLatLng = new LatLng(lat, lng);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(locationLatLng).title(address);
            mMap.addMarker(markerOptions);

            cursor.moveToNext();
            Log.d(TAG, "onLoadFinished: called Data Shown on map" );

        }

        if (locationCount>0){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

}

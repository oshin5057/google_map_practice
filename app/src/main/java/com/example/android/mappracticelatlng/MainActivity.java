package com.example.android.mappracticelatlng;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mappracticelatlng.data.LatLngContract;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE = 101;
    private Uri mCurrentLatLngUri;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLat;
    private TextView texLng;

    private TextView textAddress;

    private ResultReceiver resultReceiver;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultReceiver = new AddressResultReceiver(new Handler());

        textLat = (TextView) findViewById(R.id.textLat);
        texLng = (TextView) findViewById(R.id.textLng);

        textAddress = (TextView) findViewById(R.id.textAddress);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Intent intent = getIntent();
        mCurrentLatLngUri = intent.getData();

        findViewById(R.id.btnGetCurrentLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );

                } else {
                    getCurrentLocation();
                }
            }
        });

        findViewById(R.id.btn_add_current_latlng).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLatLng();
                Log.d(TAG,"Data Saved");
            }
        });

        findViewById(R.id.btn_SHOW_LAT_LNG).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentDisplayLatLng = new Intent(MainActivity.this, SingleLatLngDisplay.class);
                startActivity(intentDisplayLatLng);

            }
        });
    }

    private void saveLatLng() {

        String latitude = textLat.getText().toString().trim();
        String longitude = texLng.getText().toString().trim();
        String address = textAddress.getText().toString().trim();

        Log.d(TAG, "Latitude: " + latitude  );

        if (mCurrentLatLngUri == null && TextUtils.isEmpty(latitude)
                && TextUtils.isEmpty(longitude) && TextUtils.isEmpty(address)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(LatLngContract.LatLngEntry.COLUMN_LATITUDE, latitude);
        values.put(LatLngContract.LatLngEntry.COLUMN_LONGITUDE, longitude);
        values.put(LatLngContract.LatLngEntry.COLUMN_ADDRESS, address);

        Log.d(TAG, " ContentValues Latitude: " + latitude );
        Log.d(TAG, " ContentValues Longitude: " + longitude);
        Log.d(TAG, "ContentValues Address: " + address);


        if (mCurrentLatLngUri == null){
            Uri newUri = getContentResolver().insert(LatLngContract.LatLngEntry.CONTENT_URI, values);
            if (newUri == null){
                Toast.makeText(this, R.string.error_with_saving_lat_lng, Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
            else {
                Toast.makeText(this, R.string.save_latlng , Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied !!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {

        progressBar.setVisibility(View.VISIBLE);

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);

                        if (locationRequest != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            textLat.setText(
                                    String.format(
                                            "Latitude: %s",
                                            latitude
                                    )
                            );

                            texLng.setText(
                                    String.format(
                                            "Longitude: %s",
                                            longitude
                                    )
                            );

                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAddressFromLatLng(location);

                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, Looper.getMainLooper());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && requestCode == Activity.RESULT_OK){
            //fetchAllLatLng();
        }
    }

    private void fetchAddressFromLatLng(Location location){
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (resultCode == Constants.SUCCESS_RESULT){
                textAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            }
            else {
                Toast.makeText(MainActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.GONE);
        }
    }

}
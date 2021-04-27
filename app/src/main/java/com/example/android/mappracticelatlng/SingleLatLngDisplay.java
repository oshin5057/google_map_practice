package com.example.android.mappracticelatlng;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.mappracticelatlng.adpter.LatLngAdapter;
import com.example.android.mappracticelatlng.adpter.LatLngListener;
import com.example.android.mappracticelatlng.data.LatLngContract;
import com.example.android.mappracticelatlng.model.LatLngObject;

import java.util.ArrayList;
import java.util.List;

public class SingleLatLngDisplay extends AppCompatActivity implements LatLngListener {

    private LatLngAdapter mAdapter;

    private List<LatLngObject> latLngObjects = new ArrayList<>();

    private static final String TAG = "SingleLatLngDisplay";

    private Uri mCurrentLatLngUri;

    Button btn_show_map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_recycler_view_list);

        btn_show_map = (Button) findViewById(R.id.btn_show_all_lat_lng_on_map);

        Intent intent = getIntent();
        mCurrentLatLngUri = intent.getData();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new LatLngAdapter(latLngObjects, this);
        recyclerView.setAdapter(mAdapter);

        btn_show_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(SingleLatLngDisplay.this, MapActivity.class);
                startActivity(mapIntent);
            }
        });

        fetchAllLatLng();

    }

    private void fetchAllLatLng() {

        Cursor cursor = null;

        String[] projection = {
                LatLngContract.LatLngEntry._ID,
                LatLngContract.LatLngEntry.COLUMN_LATITUDE,
                LatLngContract.LatLngEntry.COLUMN_LONGITUDE,
        };

        try {
            cursor = getContentResolver().query(LatLngContract.LatLngEntry.CONTENT_URI, projection, null, null, null);
            while (cursor != null && cursor.moveToNext()){
                int idColumnIndex = cursor.getColumnIndex(LatLngContract.LatLngEntry._ID);
                int latitudeIndex = cursor.getColumnIndex(LatLngContract.LatLngEntry.COLUMN_LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(LatLngContract.LatLngEntry.COLUMN_LONGITUDE);

                int id = cursor.getInt(idColumnIndex);
                String lat = cursor.getString(latitudeIndex);
                String lng = cursor.getString(longitudeIndex);

                LatLngObject latLngObject1 = new LatLngObject();
                latLngObject1.mLatitude = lat;
                latLngObject1.mLongitude = lng;
                latLngObject1.mCursorId = id;
                latLngObjects.add(latLngObject1);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null){
                cursor.close();
            }
        }

        mAdapter.setLatLngData(latLngObjects);
        Log.d(TAG, " Fetch Latitude: " + latLngObjects);

    }

    @Override
    public void onDelete(int position, int cursorId) {

        Log.d(TAG, "onDelete: called, justCalled");

        mCurrentLatLngUri = ContentUris.withAppendedId(LatLngContract.LatLngEntry.CONTENT_URI, cursorId);
        getContentResolver().delete(mCurrentLatLngUri, null, null);
        latLngObjects.remove(position);
        mAdapter.setLatLngData(latLngObjects);
        Log.d(TAG, "onDelete: called, successfully");
    }

}

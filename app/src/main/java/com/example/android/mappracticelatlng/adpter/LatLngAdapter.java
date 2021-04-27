package com.example.android.mappracticelatlng.adpter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.mappracticelatlng.R;
import com.example.android.mappracticelatlng.model.LatLngObject;

import java.util.List;

public class LatLngAdapter extends RecyclerView.Adapter<LatLngAdapter.Holder> {

    private List<LatLngObject> latLngObjects;
    private LatLngListener latLngListener;

    private static final String TAG = "LatLngAdapter";

    public LatLngAdapter(List<LatLngObject> latLngs1Object, LatLngListener listener){
        this.latLngObjects = latLngs1Object;
        this.latLngListener = listener;
    }


    @NonNull
    @Override
    public LatLngAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_lat_lng_data, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LatLngAdapter.Holder holder, final int position) {

        holder.tvLatitude.setText(latLngObjects.get(position).mLatitude);
        holder.tvLongitude.setText(latLngObjects.get(position).mLongitude);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClickButton, Clicked");

                if (latLngListener != null){
                    Log.d(TAG, "onClick: called, LatLngAdapter");

                    latLngListener.onDelete(position, latLngObjects.get(position).mCursorId);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return latLngObjects.size();
    }

    public void setLatLngData(List<LatLngObject> latLngObjectData){
        this.latLngObjects = latLngObjectData;
        notifyDataSetChanged();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView tvLatitude;
        TextView tvLongitude;
        Button btnDelete;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvLatitude = (TextView) itemView.findViewById(R.id.tv_latitude_data);
            tvLongitude = (TextView) itemView.findViewById(R.id.tv_longitude_data);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
        }
    }
}

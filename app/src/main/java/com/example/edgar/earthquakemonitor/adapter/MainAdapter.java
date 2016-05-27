package com.example.edgar.earthquakemonitor.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edgar.earthquakemonitor.R;
import com.example.edgar.earthquakemonitor.tools.Interfaces;
import com.example.edgar.earthquakemonitor.tools.Tools;

import java.util.ArrayList;

/**
 * Created by Edgar Valeriano on 5/26/16.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.Holder> {

    private ArrayList<?> items = null;
    private Interfaces.OnItemClick response;
    private int requestCode;
    private Context context;

    private MainAdapter() {

    }

    public MainAdapter(Context context, Interfaces.OnItemClick response, int requestCode, ArrayList<?> items) {
        this.items = items;
        this.response = response;
        this.requestCode = requestCode;
        this.context = context;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_features, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        Bundle item = (Bundle) items.get(position);

        Bundle properties = item.getBundle("properties");


        String mag = properties.get("mag").toString();
        String place = properties.getString("place", "");


        holder.magnitude.setText(mag);
        holder.place.setText(place);

        int color = Tools.getColor(mag);

        holder.color.setBackgroundColor(color);

    }


    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }

        return 0;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout row;
        TextView magnitude;
        TextView place;
        FrameLayout color;


        public Holder(View view) {
            super(view);

            row = (LinearLayout) view.findViewById(R.id.row);
            magnitude = (TextView) view.findViewById(R.id.tvMagnitude);
            place = (TextView) view.findViewById(R.id.tvPlace);
            color = (FrameLayout) view.findViewById(R.id.flColor);

            row.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            response.OnItemClick(requestCode, getAdapterPosition(), items.get(getAdapterPosition()));
        }
    }


}
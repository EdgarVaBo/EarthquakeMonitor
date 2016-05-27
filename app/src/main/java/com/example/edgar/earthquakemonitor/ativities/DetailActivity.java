package com.example.edgar.earthquakemonitor.ativities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edgar.earthquakemonitor.R;
import com.example.edgar.earthquakemonitor.tools.Download;
import com.example.edgar.earthquakemonitor.tools.Interfaces;
import com.example.edgar.earthquakemonitor.tools.Tools;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, Interfaces.OnResponse, OnMapReadyCallback {

    private SwipeRefreshLayout srl;
    private static String url;
    private GoogleMap gMap;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        initViews();
        onRefresh();
        initMap();
    }

    private void initMap() {


        MapFragment mapFragment = MapFragment.newInstance();
        mapFragment.getMapAsync(this);
        getFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();


    }


    private void initViews() {

        url = getIntent().getStringExtra("detail");

        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
        srl.setOnRefreshListener(this);


        findViewById(R.id.refresh).setOnClickListener(this);
        findViewById(R.id.myLocation).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.refresh:
                onRefresh();
                break;
            case R.id.myLocation:
                if (gMap != null) {
                    LatLng latLng = new LatLng(lat, lon);
                    CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);

                    gMap.animateCamera(mCameraUpdate);
                }
                break;
        }
    }


    @Override
    public void onRefresh() {


        srl.setRefreshing(true);
        if (Tools.checkInternetConnection(this)) {
            //llamar web service

            Download.get(this, 0, url);

        } else {
            setData();
        }
    }

    private void setData() {


        String data = Tools.getSharedPreferences(this).getString(url, "");
        if (data.equals("")) {
            Toast.makeText(this, "There is no data, try again", Toast.LENGTH_SHORT).show();
            return;
        }


        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle datos = null;
        try {
            datos = Tools.createBundle(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<Bundle> origin = datos.getBundle("properties")
                .getBundle("products")
                .getParcelableArrayList("origin");

        Bundle properties = origin.get(0).getBundle("properties");
//                .getBundle("properties");

        String dateTime = properties.getString("eventtime", "");


        String magnitude = properties.getString("magnitude", "");
        String date = Tools.getDateMundialToLocal(dateTime);
        String time = Tools.getTimeMundialToLocal(dateTime);
        String latitude = properties.getString("latitude", "");
        String longitude = properties.getString("longitude", "");
        String depth = properties.getString("depth", "");


        ((TextView) findViewById(R.id.tvMagnitude)).setText(magnitude);
        ((TextView) findViewById(R.id.tvDate)).setText(date);
        ((TextView) findViewById(R.id.tvTime)).setText(time);
        ((TextView) findViewById(R.id.tvLatitude)).setText(latitude);
        ((TextView) findViewById(R.id.tvLongitude)).setText(longitude);
        ((TextView) findViewById(R.id.tvDepth)).setText(depth);


        if (gMap != null) {
            lat = Double.parseDouble(latitude);
            lon = Double.parseDouble(longitude);
            LatLng latLng = new LatLng(lat, lon);
            CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);

            gMap.animateCamera(mCameraUpdate);
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(latLng));

        }

        srl.setRefreshing(false);
    }

    @Override
    public void onResponse(int handlerCode, Object o) {
        switch (handlerCode) {

            case 0:

                if (o != null) {


                    JSONObject data = (JSONObject) o;
                    Tools.getSharedPreferences(this).edit().putString(url, data.toString()).apply();

                    setData();
                }


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }
}

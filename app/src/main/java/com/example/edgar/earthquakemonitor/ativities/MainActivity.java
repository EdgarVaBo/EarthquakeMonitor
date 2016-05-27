package com.example.edgar.earthquakemonitor.ativities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edgar.earthquakemonitor.R;
import com.example.edgar.earthquakemonitor.adapter.MainAdapter;
import com.example.edgar.earthquakemonitor.tools.Download;
import com.example.edgar.earthquakemonitor.tools.Interfaces;
import com.example.edgar.earthquakemonitor.tools.Tools;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends Activity implements Interfaces.OnResponse, SwipeRefreshLayout.OnRefreshListener, Interfaces.OnItemClick, View.OnClickListener, OnMapReadyCallback {

    private SwipeRefreshLayout srl;
    private static final String DATA_KEY = "DATA_KEY";
    private GoogleMap gMap;
    private ArrayList<Bundle> features;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initViews();
        onRefresh();
        initMap();
    }

    private void initMap() {


        MapFragment mapFragment = MapFragment.newInstance();
        mapFragment.getMapAsync(this);
        getFragmentManager().beginTransaction().replace(R.id.map1, mapFragment).commit();


    }


    private void initViews() {
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
        srl.setOnRefreshListener(this);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        findViewById(R.id.refresh).setOnClickListener(this);
        findViewById(R.id.showMap).setOnClickListener(this);
        findViewById(R.id.myLocation).setOnClickListener(this);

    }


    private void setData() {

        String data = Tools.getSharedPreferences(this).getString(DATA_KEY, "");
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


        Bundle metadata = datos.getBundle("metadata");
        String title = metadata.getString("title", "");

        ((TextView) findViewById(R.id.tvHeaderTitle)).setText(title);

        features = datos.getParcelableArrayList("features");

        MainAdapter adapter = new MainAdapter(this, this, 0, features);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        srl.setRefreshing(false);


    }

    private void loadMarkers() {
        if (gMap != null) {
            Log.i("", "");

            gMap.clear();
            CameraUpdate mCameraUpdate;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Bundle item : features) {
                Bundle geometri = item.getBundle("geometry");
                ArrayList<Bundle> coordinates = geometri.getParcelableArrayList("coordinates");


                Object lonObj = coordinates.get(0);
                Object latObj = coordinates.get(1);

                double lon = Double.parseDouble(lonObj.toString());
                double lat = Double.parseDouble(latObj.toString());

                LatLng latLng = new LatLng(lat, lon);

                builder.include(latLng);

                Bundle properties = item.getBundle("properties");
                String mag = properties.get("mag").toString();


                gMap.addMarker(new MarkerOptions().position(latLng).icon(getMarkerIcon(mag)));


            }

            LatLngBounds bounds = builder.build();
            mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 80);
            gMap.animateCamera(mCameraUpdate);
        }
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Tools.getColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    @Override
    public void onResponse(int handlerCode, Object o) {

        switch (handlerCode) {

            case 0:

                if (o != null) {


                    JSONObject data = (JSONObject) o;
                    Tools.getSharedPreferences(this).edit().putString(DATA_KEY, data.toString()).apply();

                    setData();
                }

                break;

        }
    }

    @Override
    public void onRefresh() {

        srl.setRefreshing(true);
        if (Tools.checkInternetConnection(this)) {
            //llamar web service
            String url = getString(R.string.feedUrl);
            Download.get(this, 0, url);

        } else {
            setData();
        }

    }

    @Override
    public void OnItemClick(int handlerCode, int position, Object o) {

        Bundle item = (Bundle) o;

        Bundle properties = item.getBundle("properties");
        String detail = properties.getString("detail");

        Intent in = new Intent(this, DetailActivity.class);
        in.putExtra("detail", detail);
        startActivity(in);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.myLocation:
                loadMarkers();
                break;
            case R.id.refresh:

                if (!srl.isRefreshing()) {
                    onRefresh();
                }
                break;

            case R.id.showMap:
                if (findViewById(R.id.content).getVisibility() == View.GONE) {
                    findViewById(R.id.content).animate()
//                        .translationY(-findViewById(R.id.srl).getHeight())
                            .alpha(1.0f).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            findViewById(R.id.content).setVisibility(View.VISIBLE);
//

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loadMarkers();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                } else {

                    findViewById(R.id.content).animate()
//                        .translationY(-findViewById(R.id.srl).getHeight())
                            .alpha(0.0f).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            findViewById(R.id.content).setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                }


                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }
}

package com.example.edgar.earthquakemonitor.ativities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.edgar.earthquakemonitor.R;
import com.example.edgar.earthquakemonitor.adapter.MainAdapter;
import com.example.edgar.earthquakemonitor.tools.Download;
import com.example.edgar.earthquakemonitor.tools.Interfaces;
import com.example.edgar.earthquakemonitor.tools.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends Activity implements Interfaces.OnResponse, SwipeRefreshLayout.OnRefreshListener, Interfaces.OnItemClick, View.OnClickListener {

    private SwipeRefreshLayout srl;
    private static final String DATA_KEY = "DATA_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initViews();
        onRefresh();
    }


    private void initViews() {
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
        srl.setOnRefreshListener(this);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        findViewById(R.id.refresh).setOnClickListener(this);

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


        Log.i("", datos.toString());

        ArrayList<Bundle> features = datos.getParcelableArrayList("features");

        MainAdapter adapter = new MainAdapter(this, this, 0, features);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        srl.setRefreshing(false);
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
            case R.id.refresh:

                if (!srl.isRefreshing()) {
                    onRefresh();
                }
                break;
        }
    }
}

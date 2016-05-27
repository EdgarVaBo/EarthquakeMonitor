package com.example.edgar.earthquakemonitor.tools;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by Edgar Valeriano on 5/26/16.
 */
public class Download {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Interfaces.OnResponse response, int request, String url) {
        String urlS = url.replaceAll("\\s+", "");
        client.setTimeout(10 * 1000);
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.get(urlS, new DownloadHandler(response, request));
    }

    public static void post(Context context, String url, String json, Interfaces.OnResponse respuesta, int request) {

        client.setTimeout(10 * 1000);

        StringEntity entity;
        try {
            entity = new StringEntity(json, "UTF-8");
            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (Exception e) {
            entity = null;
        }

        if (entity != null) {
            client.addHeader("Accept", "application/json");
            client.addHeader("Content-Type", "application/json");
            client.post(context, url, entity, "application/json", new DownloadHandler(respuesta, request));

        }
    }


}

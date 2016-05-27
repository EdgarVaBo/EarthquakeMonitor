package com.example.edgar.earthquakemonitor.tools;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class DownloadHandler extends JsonHttpResponseHandler {

    private Interfaces.OnResponse mOnResponse;
    private int mHandlerCode;

    public DownloadHandler(Interfaces.OnResponse handler, int tag) {
        mOnResponse = handler;
        mHandlerCode = tag;
    }

    private void handleSuccess(JSONObject response) {
        mOnResponse.onResponse(mHandlerCode, response);
    }

    private void handleFailure(String failure) {

        mOnResponse.onResponse(mHandlerCode, null);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        handleSuccess(response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        handleFailure(null);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        handleFailure(null);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        handleFailure(null);
    }
}

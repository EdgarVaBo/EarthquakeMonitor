package com.example.edgar.earthquakemonitor.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * Created by Edgar Valeriano on 5/26/16.
 */
public class Tools {



    public static Boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static Bundle createBundle(JSONObject obj) throws JSONException {

        Bundle data = new Bundle();
        Iterator<String> iter = obj.keys();

        while (iter.hasNext()) {
            String key = iter.next();

            if (obj.get(key) instanceof String) {
                data.putString(key, obj.getString(key));
            } else if (obj.get(key) instanceof Integer) {
                data.putInt(key, obj.getInt(key));
            } else if (obj.get(key) instanceof Double) {
                data.putDouble(key, obj.getDouble(key));
            } else if (obj.get(key) instanceof Boolean) {
                data.putBoolean(key, obj.getBoolean(key));
            } else if (obj.get(key) instanceof JSONObject) {
                data.putBundle(key, createBundle(obj.getJSONObject(key)));
            } else if (obj.get(key) instanceof JSONArray) {

                JSONArray innerArray = obj.getJSONArray(key);

                if (innerArray != null && innerArray.length() > 0) {

                    ArrayList<Bundle> bElements = new ArrayList<>();
                    ArrayList<String> sElements = new ArrayList<>();
                    Bundle bInnerData;


                    for (int j = 0; j < innerArray.length(); j++) {

                        if (innerArray.get(j) instanceof JSONObject) {
                            bInnerData = createBundle(innerArray.getJSONObject(j));
                            bElements.add(bInnerData);
                        } else if (innerArray.get(j) instanceof String) {
                            sElements.add(innerArray.getString(j));
                        }
                    }

                    if (bElements.size() > 0) {
                        data.putParcelableArrayList(key, bElements);
                    }

                    if (sElements.size() > 0) {
                        data.putStringArrayList(key, sElements);
                    }
                }
            }
        }

        if (data.isEmpty()) {
            return null;
        }

        return data;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Preferences", Activity.MODE_PRIVATE);
        return pref;
    }

    public static String getDateMundialToLocal(String date) {

        try {

            DateTime gmt = new DateTime(date);
            TimeZone tz = TimeZone.getDefault();

            DateTimeZone timeZone = DateTimeZone.forID(tz.getID());

            DateTime localTime = gmt.withZone(timeZone);

            int day = localTime.getDayOfMonth();
            int month = localTime.getMonthOfYear();
            int year = localTime.getYear();


            date = getMonth(month) + " " + day + ", " + year;
        } catch (Exception e) {
            date = "N/A";
            e.printStackTrace();
        }

        return date;
    }

    public static String getTimeMundialToLocal(String date) {

        try {

            DateTime gmt = new DateTime(date);
            TimeZone tz = TimeZone.getDefault();

            DateTimeZone timeZone = DateTimeZone.forID(tz.getID());

            DateTime localTime = gmt.withZone(timeZone);


            int hour = localTime.getHourOfDay();
            int min = localTime.getMinuteOfHour();

            String H = hour + "";
            String M = min + "";
            if (hour < 10)
                H = "0" + H;
            if (min < 10)
                M = "0" + M;

            date = H + ":" + M + " hrs";
        } catch (Exception e) {
            date = "N/A";
            e.printStackTrace();
        }

        return date;
    }

    public static String getMonth(int m) {
        String month = "";
        switch (m) {
            case 1:
                month = "Ene";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Abr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Ago";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dic";
                break;
        }
        return month;
    }

}

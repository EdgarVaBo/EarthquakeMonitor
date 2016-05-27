package com.example.edgar.earthquakemonitor.tools;

/**
 * Created by Edgar Valeriano on 5/26/16.
 */
public class Interfaces {
    public interface OnResponse<T> {
        void onResponse(int handlerCode, T t);
    }
    public interface OnItemClick<T> {
        void OnItemClick(int handlerCode, int position, T t);
    }


}


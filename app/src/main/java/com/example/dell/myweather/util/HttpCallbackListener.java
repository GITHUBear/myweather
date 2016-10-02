package com.example.dell.myweather.util;

/**
 * Created by dell on 2016/10/2.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}

package com.redh00d.redh00d.Utils;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * Created by Maxime on 10/10/2014.
 */
public class RestClient {

    private static final String BASE_URL = "https://192.168.1.25:1337/api"; // maxime home

    private static AsyncHttpClient client = new AsyncHttpClient( true, 80, 443 );
    private static SyncHttpClient clientSync = new SyncHttpClient( true, 80, 443 );

    /**
     * Perform a get request
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, boolean sync) {
        Log.i("RestClient", "Start a call to " + RestClient.getAbsoluteUrl(url));
        if( sync ) clientSync.get(getAbsoluteUrl(url), params, responseHandler);
        else client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Perform a post request
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, boolean sync) {
        Log.i("RestClient", "Start a call to " + RestClient.getAbsoluteUrl(url) );
        if( sync ) clientSync.post(getAbsoluteUrl(url), params, responseHandler);
        else client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}

package ksd.com.itrack;

/**
 * Created by test2 on 14/11/19.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import ksd.com.itrack.common.ILog;

public class KSDNetCheck {


    public interface ConnectState{
        public void connectSuccess();
        public void connectFail();
    }
    private static RequestQueue mQueue = null;

    public static void isNetGood(Context context, final ConnectState success,String url){
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        StringRequest req = new StringRequest(Request.Method.GET,"http://www.baidu.com",new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(success != null){
                    success.connectSuccess();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(success != null){
                    success.connectFail();
                }
            }
        });
        mQueue.add(req);
    }

    public static void hack(Context context){
        /*if(mQueue == null){
            mQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        StringRequest req = new StringRequest(Request.Method.POST,"http://112.124.6.134:9100/api/VerificationCode",new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ILog.w("response",s);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ILog.w("response",volleyError.getMessage());
            }
        }){
        @Override
        protected Map<String,String> getParams(){
            Map<String,String> params = new HashMap<String, String>();
            params.put("phone","18617149851");
            return params;
        }};

        mQueue.add(req);*/
    }
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }
}

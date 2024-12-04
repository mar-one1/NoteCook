package com.example.notecook.Utils;

import static com.example.notecook.Utils.Constants.MODE_ONLINE;
import static com.example.notecook.Utils.Constants.isConnected;
import static com.example.notecook.Utils.Constants.showToast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (checkInternet(context)) {
            showToast(context, "Network Available Do operations");
            MODE_ONLINE = true;
        } else{
            showToast(context, "No Network Available Do operations offline");
        MODE_ONLINE = false; }
    }

    boolean checkInternet(Context context) {
        // return service.isNetworkAvailable(context);
        return isConnected(context);
    }
}
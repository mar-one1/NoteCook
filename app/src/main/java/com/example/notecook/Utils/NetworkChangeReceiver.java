package com.example.notecook.Utils;

import static com.example.notecook.Utils.Constants.MODE_ONLINE;
import static com.example.notecook.Utils.Constants.getRootViewFromContext;
import static com.example.notecook.Utils.Constants.isConnected;
import static com.example.notecook.Utils.Constants.showSnackPar;
import static com.example.notecook.Utils.Constants.showToast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        MODE_ONLINE = checkInternet(context);
    }

    boolean checkInternet(Context context) {
        // return service.isNetworkAvailable(context);
        String message = isConnected(context) ? "✅ Network Available" : "❌ No Network Connection";
        showSnackPar(getRootViewFromContext(context), message);
        return isConnected(context);
    }
}
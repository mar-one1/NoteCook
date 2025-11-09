package com.example.notecook.Utils;

import static com.example.notecook.Utils.Constants.getRootViewFromContext;
import static com.example.notecook.Utils.Constants.isConnected;
import static com.example.notecook.Utils.Constants.showSnackPar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private SharedRecipeViewModel viewModel;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SharedRecipeViewModel.class);
        viewModel.setModeOnline(checkInternet(context));
    }

    boolean checkInternet(Context context) {
        // return service.isNetworkAvailable(context);
        String message = isConnected(context) ? "✅ Network Available" : "❌ No Network Connection";
        showSnackPar(getRootViewFromContext(context), message);
        return isConnected(context);
    }
}
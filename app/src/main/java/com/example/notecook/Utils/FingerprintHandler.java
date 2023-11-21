package com.example.notecook.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.notecook.R;

import me.aflak.libraries.FingerprintCallback;
import me.aflak.libraries.FingerprintDialog;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private FingerprintDialog listner;
    private CancellationSignal cancellationSignal;


    // Constructor
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        Toast.makeText(context,
                "Authentication error\n" + errString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        Toast.makeText(context,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Toast.makeText(context,
                "Authentication Success.",
                Toast.LENGTH_LONG).show();
        Constants.fingerprint_id = true;

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(context,
                "Authentication failed.",
                Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void dialogfinger() {
        FingerprintDialog.initialize(context)
                .title("Use Fingerprint")
                .message("message")
                .cancelOnTouchOutside(true)
                .errorColor(R.color.red)
                .callback(new FingerprintCallback() {
                    @Override
                    public void onAuthenticationSuccess() {
                        //loginclk();

                    }

                    @Override
                    public void onAuthenticationCancel() {


                    }
                }).show();
    }


}



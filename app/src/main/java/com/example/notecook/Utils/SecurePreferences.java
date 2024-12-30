package com.example.notecook.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecurePreferences {
    private static SharedPreferences sSharedPreferences;
    public static SharedPreferences getEncryptedSharedPreferences(Context context) throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                "secret_shared_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        return sharedPreferences;
    }

    public static void getInstanceEncryptedSharedPreferences(Context context) {
        try {
            sSharedPreferences = getEncryptedSharedPreferences(context.getApplicationContext());
        } catch (GeneralSecurityException | IOException e) {
            e.fillInStackTrace();
        }
    }

    public static void savePrefData(String TAG,Boolean toEncrypted) {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putBoolean(TAG, toEncrypted);
        editor.apply();
    }

    public static String getFromEncryptedSharedPreferences(String key) {
        return sSharedPreferences.getString(key, "");
    }
}

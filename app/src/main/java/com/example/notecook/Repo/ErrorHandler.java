package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.TAG_ERREUR_SYSTEM;
import static com.example.notecook.Utils.Constants.TAG_INFO_ERONEE;
import static com.example.notecook.Utils.Constants.TAG_NOT_FOUND;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.TAG_PAS_RESULTAT;
import static com.example.notecook.Utils.Constants.TAG_SERVEUR_HORS_SERVICE;
import static com.example.notecook.Utils.Constants.alertDialog;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.notecook.Api.ValidationError;
import com.example.notecook.Utils.Constants;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Response;

public class ErrorHandler {
    private static Set<String> errorMessages = new HashSet<>();
    public static void handleErrorResponse(Response<?> response, Activity appCompatActivity) {
        int statusCode = response.code();
        String message = response.message();
        if (response.errorBody() != null) {
            if (errorMessages.contains(message)) {
                // If the error message is already in the set, return
                return;
            } else {
                // Add the error message to the set
                errorMessages.add(message);
            }

            if (Constants.alertDialog != null && Constants.alertDialog.isShowing() && alertDialog.getTitleText().equals(String.valueOf(statusCode))) {
                Constants.alertDialog.dismiss();
                return;
            }

            if (statusCode == 400) {
                try {
                    String errorBody = response.errorBody().string();
                    Gson gson = new Gson();
                    ValidationError validationError = gson.fromJson(errorBody, ValidationError.class);
                    StringBuilder errorMessages = new StringBuilder();
                    for (ValidationError.ValidationErrorItem error : validationError.getErrors()) {
                        errorMessages.append(", ").append(error.getMessage());
                    }
                    Constants.AffichageMessage(errorMessages.toString(), "400", appCompatActivity);
                } catch (IOException e) {
                    // Handle error parsing error body
                }
            } else if (statusCode == 409) {
                Constants.AffichageMessage("Record already exists", "409", appCompatActivity);
            } else if (statusCode == 404) {
                Constants.AffichageMessage(TAG_NOT_FOUND, "404", appCompatActivity);
            } else if (statusCode == 401) {
                Constants.AffichageMessage(TAG_INFO_ERONEE, "401", appCompatActivity);
            } else if (statusCode == 500) {
                Constants.AffichageMessage(TAG_ERREUR_SYSTEM, "500", appCompatActivity);
            } else if (statusCode == 406) {
                Constants.AffichageMessage(TAG_PAS_RESULTAT, "406", appCompatActivity);
            } else if (statusCode == 502) {
                Constants.AffichageMessage(TAG_SERVEUR_HORS_SERVICE, "502", appCompatActivity);
            } else
                Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, "message", appCompatActivity);
        }

    }

    public static void handleNetworkFailure(Throwable t, Context context) {
        TAG_CONNEXION_MESSAGE = t.toString();
        if (t instanceof SocketTimeoutException) {
            System.out.println("Timeout occurred");
        } else {
            t.printStackTrace();
        }
        Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();
    }
}

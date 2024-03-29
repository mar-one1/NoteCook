package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.TAG_ERREUR_SYSTEM;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.TAG_PAS_RESULTAT;
import static com.example.notecook.Utils.Constants.TAG_SERVEUR_HORS_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.notecook.Api.ValidationError;
import com.example.notecook.Utils.Constants;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Response;

public class ErrorHandler {
        public static void handleErrorResponse(Response<?> response, Activity appCompatActivity) {
            int statusCode = response.code();
            String message = response.message();
            if (response.errorBody() != null) {
                if (statusCode == 400) {
                    try {
                        String errorBody = response.errorBody().string();
                        Gson gson = new Gson();
                        ValidationError validationError = gson.fromJson(errorBody, ValidationError.class);
                        // Now you have the validation errors in the validationError object
                        // Handle them accordingly
                        StringBuilder errorMessages = new StringBuilder();
                        for (ValidationError.ValidationErrorItem error : validationError.getErrors()) {
                            errorMessages.append(", ").append(error.getMessage());
                        }
                        Constants.AffichageMessage(errorMessages.toString(), appCompatActivity);
                    } catch (IOException e) {
                        // Handle error parsing error body
                    }
                    // Unauthorized, handle accordingly (e.g., reauthentication).
                } else if (statusCode == 409) {
                    Constants.AffichageMessage("Record already exists", appCompatActivity);
                    // Unauthorized, handle accordingly (e.g., reauthentication).
                } else if (statusCode == 404) {
                    // Not found, handle accordingly (e.g., show a 404 error message).
                    Constants.AffichageMessage(TAG_OFFLINE, appCompatActivity);
                } else if (statusCode == 500) {
                    // Handle other status codes or generic error handling.
                    Constants.AffichageMessage(TAG_ERREUR_SYSTEM, appCompatActivity);
                } else if (statusCode == 406) {
                    // Handle other status codes or generic error handling.
                    Constants.AffichageMessage(TAG_PAS_RESULTAT, appCompatActivity);
                } else if (statusCode == 502) {
                    // Handle other status codes or generic error handling.
                    Constants.AffichageMessage(TAG_SERVEUR_HORS_SERVICE, appCompatActivity);
                } else Constants.AffichageMessage(message, appCompatActivity);
            }
    }
    private void handleNetworkFailure(Throwable t, Context context) {
        // Handle network failure
        TAG_CONNEXION_MESSAGE = t.toString();
        // Handle network failure or timeout
        if (t instanceof SocketTimeoutException) {
            // Handle timeout exception
            System.out.println("Timeout occurred");
        } else {
            // Handle other network failures
            t.printStackTrace();
        }
        //Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, context);
        Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();
    }

}

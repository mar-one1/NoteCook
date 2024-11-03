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

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Response;

public class ErrorHandler {
    private static Set<String> errorMessagesSet = new HashSet<>();
    private static StringBuilder errorMessages = new StringBuilder();

    public static void handleErrorResponse(Response<?> response, Activity appCompatActivity) {
        int statusCode = response.code();
        String message = response.message();

        if (response.errorBody() != null) {
            if (errorMessagesSet.contains(message)) {
                return;
            } else {
                errorMessagesSet.add(message);
            }

            if (statusCode == 400) {
                try {
                    String errorBody = response.errorBody().string();
                    Gson gson = new Gson();
                    ValidationError validationError = gson.fromJson(errorBody, ValidationError.class);
                    for (ValidationError.ValidationErrorItem error : validationError.getErrors()) {
                        errorMessages.append(", ").append(error.getMessage());
                    }
                } catch (IOException e) {
                    // Handle error parsing error body
                }
            } else if (statusCode == 409) {
                errorMessages.append(", Record already exists");
            } else if (statusCode == 404) {
                errorMessages.append(", ").append(TAG_NOT_FOUND);
            } else if (statusCode == 401) {
                errorMessages.append(", ").append(TAG_INFO_ERONEE);
            } else if (statusCode == 500) {
                errorMessages.append(", ").append(TAG_ERREUR_SYSTEM);
            } else if (statusCode == 406) {
                errorMessages.append(", ").append(TAG_PAS_RESULTAT);
            } else if (statusCode == 502) {
                errorMessages.append(", ").append(TAG_SERVEUR_HORS_SERVICE);
            } else {
                errorMessages.append(", ").append(TAG_CONNEXION_MESSAGE);
            }

            // Show the popup if any error messages were added
            if (errorMessages.length() > 0) {
                showSweetAlertWithDismissListener(appCompatActivity);
                resetErrorMessages();
            }
        }
    }

    public static void handleNetworkFailure(Throwable t, Activity appCompatActivity) {
        String errorMessage;
        if (t instanceof SocketTimeoutException) {
            errorMessage = "Timeout occurred";
        } else {
            errorMessage = t.toString();
        }

        errorMessages.append(", ").append(errorMessage);

        // Show the popup with all accumulated network error messages
        if (errorMessages.length() > 0) {
            showSweetAlertWithDismissListener(appCompatActivity);
            //Toast.makeText(appCompatActivity, errorMessages.toString().substring(2), Toast.LENGTH_SHORT).show();
            resetErrorMessages(); // Reset after showing the toast
        }
    }

    private static void showSweetAlertWithDismissListener(Activity appCompatActivity) {
        String messagesToShow = errorMessages.toString().substring(2);

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(appCompatActivity, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText("Error")
                .setContentText(messagesToShow)
                .setConfirmText("OK")
                .setOnDismissListener(dialog -> resetErrorMessages()); // Reset when the dialog is dismissed
        sweetAlertDialog.show();
    }

    private static void resetErrorMessages() {
        errorMessages.setLength(0); // Clear the StringBuilder
        errorMessagesSet.clear(); // Clear the set of shown messages
    }
}

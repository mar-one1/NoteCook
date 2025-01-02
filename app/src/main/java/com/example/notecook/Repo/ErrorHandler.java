package com.example.notecook.Repo;

import android.app.Activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

public class ErrorHandler {
    private static Set<String> errorMessagesSet = new HashSet<>();
    private static StringBuilder errorMessages = new StringBuilder();

    // Handle error responses from the server (HTTP error codes)
    public static void handleErrorResponse(Response<?> response, Activity appCompatActivity) {
        int statusCode = response.code();
        String message = response.message();

        if (response.errorBody() != null) {
            String errorMessage = null;

            if (statusCode == 400) {
                errorMessage = "Bad request. Please check the data.";
            } else if (statusCode == 401) {
                errorMessage = "Unauthorized access. Please login again.";
            } else if (statusCode == 404) {
                errorMessage = "Resource not found.";
            } else if (statusCode == 500) {
                errorMessage = "Server error. Please try again later.";
            } else if (statusCode == 502) {
                errorMessage = "Bad gateway. The server is down.";
            } else if (statusCode == 503) {
                errorMessage = "Service unavailable. The server is temporarily down.";
            } else {
                errorMessage = "An error occurred. Please try again.";
            }

            // Show the error message if any
            if (errorMessage != null) {
                showErrorDialog(appCompatActivity, errorMessage);
            }
        }
    }

    // Handle network failures like no connection, server unreachable, or timeout
    public static void handleNetworkFailure(Throwable t, Activity appCompatActivity, Call<?> call) {
        String errorMessage;

        if (t instanceof SocketTimeoutException) {
            errorMessage = "Request timed out. The server may be taking too long to respond.";
        } else if (t instanceof IOException) {
            errorMessage = "Network error occurred. The server might be down or unreachable.";
        } else {
            errorMessage = "Unknown error occurred: " + t.getMessage();
        }

        // Cancel the request if needed (e.g., on timeout)
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        // Show error dialog
        showErrorDialog(appCompatActivity, errorMessage);
    }

    public static void handleNetworkFailure(Throwable t, Activity appCompatActivity) {
        String errorMessage;

        if (t instanceof SocketTimeoutException) {
            errorMessage = "Request timed out. The server may be taking too long to respond.";
        } else if (t instanceof IOException) {
            errorMessage = "Network error occurred. The server might be down or unreachable.";
        } else {
            errorMessage = "Unknown error occurred: " + t.getMessage();
        }

        // Show error dialog
        showErrorDialog(appCompatActivity, errorMessage);
    }

    // Display the error message in a dialog
    private static void showErrorDialog(Activity appCompatActivity, String errorMessage) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(appCompatActivity, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText("Error")
                .setContentText(errorMessage)
                .setConfirmText("OK")
                .setOnDismissListener(dialog -> {
                    // Reset any error states after the dialog is dismissed
                });
        sweetAlertDialog.show();
    }
}



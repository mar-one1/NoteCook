package com.example.notecook.Repo;

import android.app.Activity;
import android.util.Log;

import com.example.notecook.Activity.Login;
import com.example.notecook.Fragement.Frg_EditProfil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

public class ErrorHandler {
    private static final Queue<String> errorMessageQueue = new LinkedList<>();
    private static boolean isPopupVisible = false;

    // Handle error responses from the server (HTTP error codes)
    public static void handleErrorResponse(Response<?> response, Activity appCompatActivity) {
        int statusCode = response.code();
        try {
            if (response.errorBody() != null) {
            String errorMessage = response.errorBody().string();
            Log.e("error",errorMessage);
                if (errorMessage.isEmpty()) {
                    if (statusCode == 400) {
                        errorMessage = "Bad request. Please check the data.";
                    } else if (statusCode == 401) {
                        errorMessage = "Unauthorized access. Please login again.";
                        if (!appCompatActivity.getClass().equals(Login.class)) {
                            Frg_EditProfil.logOut(appCompatActivity);
                        }
                    } else if (statusCode == 404) {
                        errorMessage = "Resource not found.";
                    } else if (statusCode == 406) {
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
                }
                /*else {
                    String jsonResponse = errorMessage;
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    errorMessage = jsonObject.getString("error");
                    Log.d("API_ERROR", errorMessage);  // Output: User not found
                }*/

                addErrorMessageToQueue(errorMessage, appCompatActivity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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

        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        addErrorMessageToQueue(errorMessage, appCompatActivity);
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

        addErrorMessageToQueue(errorMessage, appCompatActivity);
    }

    // Add the error message to the queue and show it if no popup is visible
    private static void addErrorMessageToQueue(String errorMessage, Activity appCompatActivity) {
        errorMessageQueue.offer(errorMessage);
        showNextErrorMessage(appCompatActivity);
    }

    // Show the next error message in the queue
    private static void showNextErrorMessage(Activity appCompatActivity) {
        if (!isPopupVisible && !errorMessageQueue.isEmpty()) {
            String nextMessage = errorMessageQueue.poll();
            if (nextMessage != null) {
                showErrorDialog(appCompatActivity, nextMessage);
            }
        }
    }

    // Display the error message in a dialog
    private static void showErrorDialog(Activity appCompatActivity, String errorMessage) {
        isPopupVisible = true;

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(appCompatActivity, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText("Error")
                .setContentText(errorMessage)
                .setConfirmText("OK")
                .setOnDismissListener(dialog -> {
                    isPopupVisible = false;
                    showNextErrorMessage(appCompatActivity);
                });
        sweetAlertDialog.show();
    }
}

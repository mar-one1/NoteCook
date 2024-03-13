package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private ApiService apiService;
    private UserDatasource userDatasource;
    private Context context;

    public UserRepository(Context context) {
        this.context = context;
        apiService = ApiClient.getClient().create(ApiService.class);
        userDatasource = new UserDatasource(context);
    }

    public LiveData<User> getUserApi(String username, Context context) {
        MutableLiveData<User> userLogin = new MutableLiveData<>();
        // Example: Fetch users from the API
        apiService.getUserByUsername(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        UserResponse.setId_User(user_login.getUser().getId_User());
                        userLogin.setValue(UserResponse);
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "get user from Api", Toast.LENGTH_LONG).show();
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleNetworkFailure(call);
            }
        });
        return userLogin;
    }


    public LiveData<User> UpdateUserApi(User user, Context context) {
        MutableLiveData<User> userUpdated = new MutableLiveData<>();
        // Example: Fetch users from the API
        apiService.updateUserByUsername(user.getUsername(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        UserResponse.setId_User(user_login.getUser().getId_User());
                        userUpdated.setValue(UserResponse);
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "user updated To Api", Toast.LENGTH_LONG).show();
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleNetworkFailure(call);
            }
        });
        return userUpdated;
    }

    public void uploadImage(String username, Bitmap bitmp, String type, Context context) {
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.add_photo_profil); // Replace 'your_image' with the image resource name
        // Create a file to save the bitmap
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, "image.jpg"); // Change 'image.jpg' to the desired file name and format

        // Convert bitmap to file
        try {
            OutputStream os = new FileOutputStream(imageFile);
            bitmp.compress(Bitmap.CompressFormat.JPEG, 100, os); // Compress bitmap into JPEG with quality 100%
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create a File instance with the path to the file to upload
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);

// Create MultipartBody.Part instance from the RequestBody
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
// Create a service using the Retrofit interface
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
// Call the method to upload the file
        apiService.uploadFile(username, filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String path = null;
                    try {
                        path = response.body().string();
                        //String str = new String(bytes, StandardCharsets.UTF_8);
                        path = path.replaceAll("\"", "");// For UTF-8 encoding
                        if (!type.equals("register"))
                            user_login.getUser().setPathimageuser(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "upload image : " + path, Toast.LENGTH_SHORT).show();
                    // File upload successful
                    //fetchImage(path);
                    Toast.makeText(context, "upload image : " + TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();

                } else {
                    // Handle unsuccessful upload
                    Toast.makeText(context, "Not upload image : " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "OnFailure upload image : " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleErrorResponse(Response<?> response) {
        int statusCode = response.code();
        String message = response.message();
        if (response.errorBody() != null) {
            try {
                String errorResponse = response.errorBody().string();
                Log.e("ErrorResponse", "Error Response: " + errorResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNetworkFailure(Call<User> call) {
        // Handle network failure
        TAG_CONNEXION_MESSAGE = call.toString();
        //Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, context);
        Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();
    }


    public void getLocalUserLogin(String username) {
        UserDatasource userDatasource = new UserDatasource(context);
        userDatasource.open();
        user_login_local.setUser(userDatasource.select_User_BYUsername(username));
        userDatasource.close();
    }


}

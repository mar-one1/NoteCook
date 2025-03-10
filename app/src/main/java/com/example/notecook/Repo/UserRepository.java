package com.example.notecook.Repo;

import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_USERNAME;
import static com.example.notecook.Data.MySQLiteHelperTable.TABLE_USER;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Dto.TokenResponse;
import com.example.notecook.Api.ValidationError;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.Utils.PasswordHasher;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
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
    private SharedPreferences sharedPreferences;
    private Activity appCompatActivity;

    public UserRepository(Context context, Activity appCompatActivity) {
        this.context = context;
        apiService = ApiClient.getClient().create(ApiService.class);
        userDatasource = new UserDatasource(context);
        this.appCompatActivity = appCompatActivity;
    }

    public LiveData<User> getUserApi(String username) {
        MutableLiveData<User> userLogin = new MutableLiveData<>();
        // Example: Fetch users from the API
        apiService.getUserByUsername(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        //UserResponse.setId_User(user_login.getUser().getId_User());
                        //userLogin.setValue(UserResponse);
                        user_login.setUser(UserResponse);
                        userLogin.setValue(getLocalUserLogin(username, "success").getValue());
                        Log.e("tag","image url"+user_login.getUser().getPathimageuser());
                        //getImageUserUrl(user_login.getUser().getUsername(), "user_login", context);
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "get user from Api", Toast.LENGTH_LONG).show();
                    }
                } else {
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                    //handleErrorResponse(response);
                    userLogin.setValue(getLocalUserLogin(username, "").getValue());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ErrorHandler.handleNetworkFailure(t,appCompatActivity);
                userLogin.setValue(getLocalUserLogin(username, "").getValue());
            }
        });
        return userLogin;
    }

    public void getImageUserUrl(String username, String tag, Context context) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Enqueue the download request
        Call<ResponseBody> call = apiService.getImageUSerBytes(username);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //ResponseBody responseBody = response.body();
                    byte[] bytes = new byte[0];
                    try {
                        bytes = response.body().bytes();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    //user_login.getUser().setIcon(bytes);
                    // Convert byte array to String using a specific character encoding
                    String str = new String(bytes, StandardCharsets.UTF_8);
                    str = str.replaceAll("\"", "");// For UTF-8 encoding
                    Log.d("tag", str);
                    if (Objects.equals(tag, "user_login"))
                        user_login.getUser().setPathimageuser(str);
                    if (Objects.equals(tag, "recipe_user")) {
                        User_CurrentRecipe.setPathimageuser(str);
                        MainFragment.viewPager2.setCurrentItem(1, false);
                    }
                    //fetchImage(str,tag,0,context);
                    Toast.makeText(context, "succes  image down : ", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful download
                    Toast.makeText(context, "unsuccessful download" + response.message(), Toast.LENGTH_SHORT).show();
                    ErrorHandler.handleErrorResponse(response,appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(context, "Handle failure getimage url", Toast.LENGTH_SHORT).show();
                ErrorHandler.handleNetworkFailure(t,appCompatActivity);
            }
        });
    }


    public LiveData<User> UpdateUserApi(User user, Bitmap bitmap) {
        MutableLiveData<User> userUpdated = new MutableLiveData<>();
        // Example: Fetch users from the API
        apiService.updateUserByUsername(user.getUsername(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        try {
                            UserResponse.setId_User(user_login.getUser().getId_User());
                            userUpdated.setValue(UserResponse);
                        } catch (Exception e) {
                            Log.e("tag", "" + e);
                        }
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "user updated To Api", Toast.LENGTH_LONG).show();
                    }
                } else {
                    ErrorHandler.handleErrorResponse(response,appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ErrorHandler.handleNetworkFailure(t,appCompatActivity);
            }
        });
        return userUpdated;
    }

    public void deleteimage(String s) {
        // Enqueue the download request
        apiService.deleteimage(s).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Toast.makeText(context, "succes  image deleted : ", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful download
                    Toast.makeText(context, "unsuccessful deleted" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(context, "Handle failure" + t, Toast.LENGTH_SHORT).show();
                Log.d("tag", "Handle failure" + t);
            }
        });
    }

    public MutableLiveData<String>  uploadImage(String username, Bitmap bitmp,String oldPath, String type) {
        MutableLiveData<String> pathImage = new MutableLiveData<>();
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
                        pathImage.setValue(path);
                        deleteimage(oldPath);
                        if (!type.equals("register")){}
                        //user_login.getUser().setPathimageuser(path);
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
                    ErrorHandler.handleErrorResponse(response,appCompatActivity);
                }

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "OnFailure upload image : " + t.toString(), Toast.LENGTH_SHORT).show();
                ErrorHandler.handleNetworkFailure(t,appCompatActivity);
            }
        });
        return  pathImage;
    }

    public LiveData<User> getUserByIdRecipeApi(int Recipeid) {
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        apiService.getUserByIdRecipe(Token, Recipeid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    User user = response.body();
                    userMutableLiveData.setValue(user);
                    User_CurrentRecipe = user;
                    //getImageUserUrl(User_CurrentRecipe.getUsername(), "recipe_user", context);
                    Log.d("TAG", user.getUsername().toString());
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                    //MainFragment.viewPager2.setCurrentItem(1);
                } else {
                    // Handle error response here
                    ErrorHandler.handleErrorResponse(response,appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
                ErrorHandler.handleNetworkFailure(t,appCompatActivity);
            }
        });
        return userMutableLiveData;
    }




    public LiveData<User> getLocalUserLogin(String username, String tag) {
        MutableLiveData<User> user = new MutableLiveData<>();
        if (user_login == null) {
            user_login = new TokenResponse();
        }
        user.setValue(userDatasource.select_User_BYUsername(username));
        if (user_login_local == null) {
            user_login_local = new TokenResponse();
        }
        if (tag.equals("success")) {
            user_login_local.setUser(user.getValue());
        } else {
            user_login.setUser(user.getValue());
            user_login.setMessage(TAG_LOCAL);
            user_login_local.setUser(user.getValue());
        }
        ImageHelper.deleteUnusedImages(context,userDatasource.getAllUsersImagePath(),"UserImages");
        return  user;

    }

    public LiveData<User> InsertUserApi(User user, String url, Bitmap bitmap, String type) {
        MutableLiveData<User> userInsered = new MutableLiveData<>();
        // Example: Fetch users from the API
        apiService.createUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        TAG_CONNEXION = response.code();
                        TAG_CONNEXION_MESSAGE = response.message();
                        userInsered.setValue(UserResponse);
                        if (type != null && type.equals("registre"))
                            uploadImage(user.getUsername(), bitmap,"", type);
                        else if (!url.isEmpty()) {
                            updateGoogleUserImage(user.getUsername(), url).getValue();
                        }
                        Constants.AffichageMessage("Vous avez Register avec succes with server","200", appCompatActivity);
                        Log.d("TAG", TAG_CONNEXION_MESSAGE + " " + "Add User To Api");
                    }
                } else {
                    ErrorHandler.handleErrorResponse(response,appCompatActivity);

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ErrorHandler.handleNetworkFailure(t,appCompatActivity);
            }
        });
        return userInsered;
    }

    public LiveData<String> updateGoogleUserImage(String username, String path) {
        MutableLiveData<String> pathImageUser = new MutableLiveData<>();

        String jsonInputString = "{\"url\": \"" + path + "\"}";
// Create a RequestBody from the string
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), jsonInputString);

        // Call the method to upload the file
        apiService.updateUserGoogleImageUrl(username, requestBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String path = null;
                    path = response.body().toString();
                    pathImageUser.setValue(path);
                    //String str = new String(bytes, StandardCharsets.UTF_8);
                    //path = path.replaceAll("\"", "");// For UTF-8 encoding
                    Toast.makeText(context, "upload image : " + path, Toast.LENGTH_SHORT).show();
                    // File upload successful
                    //fetchImage(path);
                    Toast.makeText(context, "upload image : " + TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();

                } else {
                    // Handle unsuccessful upload
                    ErrorHandler.handleErrorResponse(response,appCompatActivity);
                    pathImageUser.setValue("");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle failure
                ErrorHandler.handleNetworkFailure(t,appCompatActivity);
                pathImageUser.setValue("");
            }
        });
        return pathImageUser;
    }

    public LiveData<User> InsertUserLocal(User user, Bitmap bitmap) {
        MutableLiveData<User> userInsered = new MutableLiveData<>();
        // Example: Fetch users from the API
        PasswordHasher passwordHasher = new PasswordHasher();
        String passwordHacher = passwordHasher.hashPassword(user.getPassWord());
        user.setPassWord(passwordHacher);
        user_login.setUser(user);
        if (!userDatasource.isRecordExist(TABLE_USER, COLUMN_USERNAME, user.getUsername())) {
            userInsered.setValue(userDatasource.insertUser(user));
            Log.e("tag", user.getUsername());
        }
        return userInsered;
    }

}

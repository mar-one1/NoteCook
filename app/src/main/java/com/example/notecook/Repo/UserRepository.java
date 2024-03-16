package com.example.notecook.Repo;

import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_ID_FRK_USER_RECIPE;
import static com.example.notecook.Data.MySQLiteHelperTable.TABLE_RECIPE;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.lOGIN_KEY;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Api.TokenResponse;
import com.example.notecook.Data.ModelsDataSource;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;

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
    private RecipeDatasource recipeDatasource;
    private Context context;
    private SharedPreferences sharedPreferences;

    public UserRepository(Context context) {
        this.context = context;
        apiService = ApiClient.getClient().create(ApiService.class);
        userDatasource = new UserDatasource(context);
        recipeDatasource = new RecipeDatasource(context);
    }

    public LiveData<User> getUserApi(String username) {
        MutableLiveData<User> userLogin = new MutableLiveData<>();
        sharedPreferences = context.getSharedPreferences(lOGIN_KEY, Context.MODE_PRIVATE);
        String s1 = sharedPreferences.getString("username", "");
        String s2 = sharedPreferences.getString("password", "");
        // Example: Fetch users from the API
        apiService.getUserByUsername(s1).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        UserResponse.setId_User(user_login.getUser().getId_User());
                        userLogin.setValue(UserResponse);
                        user_login.setUser(UserResponse);
                        getLocalUserLogin(s1, "success");
                        getImageUserUrl(user_login.getUser().getUsername(), "user_login", context);
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "get user from Api", Toast.LENGTH_LONG).show();
                    }
                } else {
                    handleErrorResponse(response);
                    getLocalUserLogin(username, "");

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleNetworkFailure(call);
                getLocalUserLogin(username, "");
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
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(context, "Handle failure getimage url", Toast.LENGTH_SHORT).show();
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
                            deleteimage(user.getPathimageuser());
                            uploadImage(user_login.getUser().getUsername(), bitmap, "register");
                        }catch (Exception e) {Log.e("tag",""+e);}
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

    public void uploadImage(String username, Bitmap bitmp, String type) {
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
        erorBody(statusCode, message);
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


    public void getLocalUserLogin(String username, String tag) {
        userDatasource.open();
        if (user_login == null) {
            user_login = new TokenResponse();
        }
        User user = userDatasource.select_User_BYUsername(username);
        if (user_login_local == null) {
            user_login_local = new TokenResponse();
        }
        if (tag.equals("success")) {
            user_login_local.setUser(user);
        } else {
            user_login.setUser(user);
            user_login.setMessage(TAG_LOCAL);
        }
        getLocalRecipes(user.getId_User());
        userDatasource.close();
    }

    private void getLocalRecipes(int id_user) {
        recipeDatasource.open();
        //ModelsDataSource<Recipe> model = new ModelsDataSource<>(context,Recipe.class);
        //list_recipe.addAll(Objects.requireNonNull(model.getAllRecordsByIdUser(TABLE_RECIPE, COLUMN_ID_FRK_USER_RECIPE, i).getValue()));
        list_recipe.setValue(recipeDatasource.getRecipeById(id_user).getValue());
        recipeDatasource.close();
        //ModelsDataSource<Recipe> model = new ModelsDataSource<>(context,Recipe.class);
        //list_recipe=model.getAllRecordsByIdUser(TABLE_RECIPE,COLUMN_ID_FRK_USER_RECIPE,id_user).getValue();
    }

    public void erorBody(int statusCode, String message) {
        if (statusCode == 409) {
            //Constants.AffichageMessage("User already exists", context);
            //Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show();
            // Unauthorized, handle accordingly (e.g., reauthentication).
        } else if (statusCode == 404) {
            // Not found, handle accordingly (e.g., show a 404 error message).
            //Constants.AffichageMessage(TAG_OFFLINE, context);
            Toast.makeText(context, TAG_OFFLINE, Toast.LENGTH_SHORT).show();
        } else if (statusCode >= 500) {
            // Handle other status codes or generic error handling.
            //Constants.AffichageMessage("Internal Server Error", context);
            Toast.makeText(context, "Internal Server Error", Toast.LENGTH_SHORT).show();
        } else if (statusCode == 406) {
            // Handle other status codes or generic error handling.
            // Constants.AffichageMessage("User not found", context);
        } else Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
        //Constants.AffichageMessage(response.message(), context);
    }


}

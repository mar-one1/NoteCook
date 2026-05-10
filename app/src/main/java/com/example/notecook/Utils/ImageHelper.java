package com.example.notecook.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageHelper {


    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
            if (bmp != null) return bmp;
        }

        int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 1;
        int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public static Bitmap decodeBase64ToBitmap(String base64Image) {
        try {
            if (base64Image == null || base64Image.isEmpty()) return null;

            // Remove data:image/...;base64, if exists
            if (base64Image.startsWith("data:image")) {
                base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
            }

            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        } catch (Exception e) {
            Log.e("DecodeError", "Base64 decode failed", e);
            return null;
        }
    }


    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static Bitmap loadImageFromPath(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(path);
        } else {
            Log.e("ImageHelper", "File not found at path: " + path);
            return null;
        }
    }

    public static String saveImageToInternalStorage(Context context, Bitmap imageBitmap, String table) {
        // Get the directory to store the image (app's private storage)
        File directory = new File(context.getFilesDir(), table);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a file to save the image
        File imageFile = new File(directory, "image_" + System.currentTimeMillis() + ".png");

        // Save the image to the file
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the absolute path of the saved image file
        return imageFile.getAbsolutePath();
    }

    public static void deleteUnusedImages(Context context, List<String> allImagePathsInDb, String table) {
        List<String> allImagePathsOnDevice = getAllImagePathsOnDevice(context, table);

        // Delete unused image files
        for (String imagePath : allImagePathsOnDevice) {
            if (!allImagePathsInDb.contains(imagePath)) {
                boolean deleted = deleteImageFile(imagePath);
                if (deleted) {
                    Log.d("ImageCleanupHelper", "Deleted unused image: " + imagePath);
                } else {
                    Log.e("ImageCleanupHelper", "Failed to delete image: " + imagePath);
                }
            }
        }
    }

    private MultipartBody.Part bitmapToMultipart(Bitmap bitmap,Context context) throws IOException {

        File file = new File(context.getCacheDir(), "avatar.jpg");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        fos.flush();
        fos.close();

        RequestBody requestBody =
                RequestBody.create(file, MediaType.parse("image/jpeg"));

        return MultipartBody.Part.createFormData(
                "image",
                file.getName(),
                requestBody
        );
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = (float) width / height;

        if (ratio > 1) {
            width = maxSize;
            height = (int) (width / ratio);
        } else {
            height = maxSize;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private static List<String> getAllImagePathsOnDevice(Context context, String table) {
        List<String> imagePaths = new ArrayList<>();
        // Specify the directory where your images are stored
        File imageDirectory = new File(context.getFilesDir(), table);
        if (imageDirectory.exists() && imageDirectory.isDirectory()) {
            File[] files = imageDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        imagePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return imagePaths;
    }

    private static boolean deleteImageFile(String imagePath) {
        File imageFile = new File(imagePath);
        return imageFile.exists() && imageFile.delete();
    }
}


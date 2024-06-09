package com.example.notecook.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.notecook.Data.MySQLiteHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageHelper {


    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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

    public static String saveImageToInternalStorage(Context context, Bitmap imageBitmap,String table) {
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

    public static void deleteUnusedImages(Context context,List<String> allImagePathsInDb,String table) {
        List<String> allImagePathsOnDevice = getAllImagePathsOnDevice(context,table);

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

    private static List<String> getAllImagePathsOnDevice(Context context,String table) {
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


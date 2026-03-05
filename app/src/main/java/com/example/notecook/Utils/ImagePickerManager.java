package com.example.notecook.Utils;


import android.graphics.Bitmap;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

public class ImagePickerManager {

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Void> cameraLauncher;

    public interface ImageResult {
        void onImagePicked(Uri uri, Bitmap bitmap);
    }

    public ImagePickerManager(Fragment fragment, ImageResult callback) {

        galleryLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        callback.onImagePicked(uri, null);
                    }
                });

        cameraLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        callback.onImagePicked(null, bitmap);
                    }
                });
    }

    public void openGallery() {
        galleryLauncher.launch("image/*");
    }

    public void openCamera() {
        cameraLauncher.launch(null);
    }
}


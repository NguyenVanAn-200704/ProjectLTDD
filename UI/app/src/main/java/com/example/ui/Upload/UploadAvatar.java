package com.example.ui.Upload;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.InputStream;
import java.util.Map;

public class UploadAvatar extends AsyncTask<Void, Void, String> {
    private Context context;
    private Uri imageUri;
    private OnAvatarUploadSuccessListener listener;

    public interface OnAvatarUploadSuccessListener {
        void onAvatarUploadSuccess(String avatarUrl);
    }

    public UploadAvatar(Context context, Uri imageUri, OnAvatarUploadSuccessListener listener) {
        this.context = context;
        this.imageUri = imageUri;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "xuandung",
                    "api_key", "727986767432948",
                    "api_secret", "jjHIMnlRgvXfYMfkvRssLqdpgms"
            ));

            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());

            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            Log.e("CloudinaryUpload", "Upload failed", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            listener.onAvatarUploadSuccess(result);
        } else {
            Log.e("UploadAvatarTask", "Upload failed");
        }
    }
}
package com.example.ui.Upload;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class UploadTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private Uri fileUri;
    private OnTaskUploadSuccessListener listener;

    private File getFileFromUri(Uri uri) throws IOException {
        String fileName = getFileName(uri);
        File tempFile = new File(context.getCacheDir(), fileName);
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        OutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();

        return tempFile;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) result = cursor.getString(index);
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }


    public interface OnTaskUploadSuccessListener {
        void onTaskUploadSuccess(String fileUrl);

        void onTaskUploadFailure(String errorMessage);
    }

    public UploadTask(Context context, Uri fileUri, OnTaskUploadSuccessListener listener) {
        this.context = context;
        this.fileUri = fileUri;
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

            File fileToUpload = getFileFromUri(fileUri);
            Log.d("UploadTask", "Uploading file: " + fileToUpload.getAbsolutePath() + ", size: " + fileToUpload.length());

            Map uploadResult = cloudinary.uploader().upload(fileToUpload, ObjectUtils.asMap(
                    "resource_type", "raw"   // ⚠️ cần dòng này để upload file .docx
            ));

            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            Log.e("CloudinaryUpload", "Upload failed", e);
            return null;
        }
    }


    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            listener.onTaskUploadSuccess(result);
        } else {
            Log.e("UploadTask", "Upload failed");
            Toast.makeText(context, "Failed to upload file", Toast.LENGTH_SHORT).show();
        }
    }
}
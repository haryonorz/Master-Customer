package com.example.mastercustomer.utility;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;

import com.example.mastercustomer.BuildConfig;
import com.example.mastercustomer.R;

import java.io.File;

public class CameraUtils {

    public static boolean checkPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Open device app settings to allow user to enable permissions
     */
    public static void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Uri getOutputMediaFileUri(Context context, File file) {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            return Uri.fromFile(file);
        } else {
            return FileProvider.getUriForFile(context,
                    context.getString(R.string.file_provider_authority), file);
        }
    }

    /**
     * Creates and returns the image or video file before opening the camera
     */
    public static File getOutputMediaFile(String filename) {

        // External sdcard location
        File mediaStorageDir;
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/MasterCustomer/PhotoOutlet/");
        }else {
            mediaStorageDir = Environment.getExternalStoragePublicDirectory("/MasterCustomer/PhotoOutlet/");
        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Preparing media file naming convention
        // adds timestamp
        File mediaFile = new File(mediaStorageDir, filename + ".jpg");
        return mediaFile;
    }
}

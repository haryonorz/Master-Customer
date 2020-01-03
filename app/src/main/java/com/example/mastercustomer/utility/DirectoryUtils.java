package com.example.mastercustomer.utility;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.example.mastercustomer.BuildConfig;

import java.io.File;

public class DirectoryUtils {

    public static boolean checkPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void createDirectory() {
        createFile(Environment.getExternalStorageDirectory().toString(), "MasterCustomer");
        createFile(Environment.getExternalStorageDirectory()+"/MasterCustomer/", "PhotoOutlet");
    }

    static void createFile(String filePath, String nameFile){
        File file = new File(filePath, nameFile);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdir();
        }
    }

    public static void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

package com.bijendra.downloadmanager.sample;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Newdream on 13-Dec-16.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = new String[]{
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE,
            INTERNET
    };

    public void checkPermissions() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!hasPermissions(requestCode, grantResults)) {
            finish();
        }
    }

    private boolean hasPermissions(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}

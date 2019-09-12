package com.appsecco.vyapi.misc;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appsecco.vyapi.MainActivity;

public class PermissionCheck {

    private static final int PERMISSION_REQUEST_CODE = 1;

    Activity activity;
    Context context;

    public PermissionCheck(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    public boolean checkCallPermission(String permission) {
        int CallPermissionResult = ContextCompat.checkSelfPermission(this.context, permission);
        return CallPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCallPermission(String permission) {
        ActivityCompat.requestPermissions(this.activity, new String[]
                {
                        permission
                }, PERMISSION_REQUEST_CODE);
    }
}

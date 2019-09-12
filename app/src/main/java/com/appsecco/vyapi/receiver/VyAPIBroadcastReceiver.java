package com.appsecco.vyapi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class VyAPIBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        Bundle bundle = intent.getExtras();

        if (intentAction != null) {
            String toastMessage = "unknown intent action";
            switch (intentAction){
                case "com.appsecco.vyapi.Broadcast":
                    toastMessage = "Explicit intent received as 'com.appsecco.vyapi.Broadcast'";
                    String new_file_name;
                    String temp_file_path;
                    String new_file_path;

                    FileInputStream in = null;
                    FileOutputStream out = null;
                    int cursor;

                    new_file_name = bundle.get("new_file_name").toString();
                    temp_file_path = bundle.get("temp_file_path").toString();

                    new_file_path = context.getFilesDir() + "/" + new_file_name;

                    try {
                        in = new FileInputStream(new File(temp_file_path));
                        out = new FileOutputStream(new_file_path);

                        while ((cursor = in.read()) != -1){
                            out.write(cursor);
                        }

                        // Rename the file in temp storage
                        File oldFile = new File(temp_file_path);
                        File newFile = new File(oldFile.getParent() + "/" + new_file_name);
                        oldFile.renameTo(newFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }

            //Display the toast.
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }
}

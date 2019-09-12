package com.appsecco.vyapi.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.appsecco.vyapi.R;
import com.appsecco.vyapi.misc.PermissionCheck;
import com.appsecco.vyapi.receiver.VyAPIBroadcastReceiver;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    private TextView textView_camera;
    private ImageView camera_imageView;
    private FloatingActionButton fab;
    private static final int REQUEST_IMAGE_CAPTURE = 111;

    public static String currentImageFilePath;
    public static VyAPIBroadcastReceiver myBroadcastReceiver = new VyAPIBroadcastReceiver();

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Check permission
        if (Build.VERSION.SDK_INT >= 23) {
            PermissionCheck permissionCheck = new PermissionCheck(getActivity(), getContext());

            if (permissionCheck.checkCallPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e("permission", "Permission already granted.");
            } else {
                permissionCheck.requestCallPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        textView_camera = view.findViewById(R.id.textView_camera);
        camera_imageView = view.findViewById(R.id.camera_imageView);
        fab = view.findViewById(R.id.camera_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureMoment();
            }
        });

        // Register Broadcast Receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.appsecco.vyapi.Broadcast");
        getActivity().registerReceiver(myBroadcastReceiver, filter);

        return view;
    }


    public void captureMoment() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            // Create a file to storoe the image
            File file = null;
            try {
                file = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri uri = null;
            if(file != null){
                try {
                    uri = FileProvider.getUriForFile(getContext(), "com.appsecco.vyapi.CaptureImageFileProvider", file);
                } catch (IllegalArgumentException e) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        uri = Uri.fromFile(file);
                    } 
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final @Nullable Intent intent) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:

                if(resultCode == getActivity().RESULT_OK){
                    if(intent != null && intent.hasExtra("data")){
                        Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                        camera_imageView.setImageBitmap(bitmap);
                    } else {
                        Glide.with(getContext()).load(new File(currentImageFilePath)).into(camera_imageView);
                        textView_camera.setText("That's pretty! Like it or not, we are saving this in secret Gallery.");
                    }

                    // Ask user to enter a name for the newly captured image
                    final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    LayoutInflater factory = LayoutInflater.from(getContext());
                    final View view_get_file_name = factory.inflate(R.layout.get_image_file_name, null);

                    alert.setView(view_get_file_name)
                        .setTitle("Name this moment:")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText et_image_file_name = view_get_file_name.findViewById(R.id.et_image_file_name);
                                String new_file_name = "";

                                new_file_name = et_image_file_name.getText().toString() + "_" + Long.toString(System.currentTimeMillis()) + ".jpg";

                                Intent intent_save_image = new Intent();
                                intent_save_image.setAction("com.appsecco.vyapi.Broadcast");
                                intent_save_image.putExtra("new_file_name", new_file_name);
                                intent_save_image.putExtra("temp_file_path", currentImageFilePath);
                                getActivity().sendBroadcast(intent_save_image);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Saving your image with a random junk name! :P", Toast.LENGTH_SHORT).show();
                            }
                        });

                    alert.show();

//                    File image_path = new File(getContext().getFilesDir(), "my_gallery");


                }

                break;
        }
    }


    private File createImageFile() throws IOException {
        String imageFileName = "IMG_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir );

        currentImageFilePath = image.getAbsolutePath();
        return image;
    }
}

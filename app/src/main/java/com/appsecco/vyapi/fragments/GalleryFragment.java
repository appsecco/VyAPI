package com.appsecco.vyapi.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.appsecco.vyapi.BuildConfig;
import com.appsecco.vyapi.R;
import com.appsecco.vyapi.misc.PermissionCheck;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    private TextView gallery_path;
    private TextView gallery_message;
    private static ImageView gallery_empty;
    private static ListView gallery_listview;

    private List<String> file_item = null;
    private List<String> file_path = null;

    public static String root;
    public static List<String> gallery_files;

    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        // Check permission
        if (Build.VERSION.SDK_INT >= 23) {
            PermissionCheck permissionCheck = new PermissionCheck(getActivity(), getContext());

            if (permissionCheck.checkCallPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.e("permission", "Permission already granted.");
            } else {
                permissionCheck.requestCallPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        gallery_message = (TextView)view.findViewById(R.id.gallery_message);
        gallery_path = (TextView) view.findViewById(R.id.gallery_path);
        gallery_empty = (ImageView) view.findViewById(R.id.gallery_empty);
        gallery_listview = (ListView) view.findViewById(R.id.gallery_listview);

        root = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        getDir(root);

        gallery_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object selectedItem = gallery_listview.getItemAtPosition(position);
                Toast.makeText(getContext(),selectedItem.toString(),Toast.LENGTH_SHORT).show();

                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                LayoutInflater factory = LayoutInflater.from(getContext());
                final View view_picture = factory.inflate(R.layout.view_picture, null);
                ImageView iv_view_picture = (ImageView) view_picture.findViewById(R.id.iv_view_picture);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final String pathname = root + "/" + selectedItem.toString();
                Bitmap bitmap = BitmapFactory.decodeFile(pathname, options);
                iv_view_picture.setImageBitmap(bitmap);

//                Uri uri = FileProvider.getUriForFile(view_picture.getContext(), "com.appsecco.vyapi.LoadImageFileProvider", new File(root + "/" + selectedItem.toString()));
//                Glide.with(view_picture).load(uri).display_data_from_db(R.drawable.call_icon).into(iv_view_picture);
                alert.setView(view_picture)
                        .setCancelable(true)
                        .setPositiveButton("Done Admiring!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("DELETE!!!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File fs = new File(pathname);
                                fs.delete();
                                getDir(root);
                            }
                        })
                        .setNeutralButton("Zoom In", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Uri uri = FileProvider.getUriForFile(getContext(), "com.appsecco.vyapi.LoadImageFileProvider", new File(pathname));
                                try {
                                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                intent.setDataAndType(Uri.parse("file://" + pathname), "image/*");
//                                intent.setDataAndType(Uri.parse("file://" + "/etc/hosts"), "*/*");
                                PackageManager pm = getActivity().getPackageManager();
                                if(intent.resolveActivity(pm) != null){
                                    startActivity(intent);
                                }
                            }
                        });

                alert.show();
            }
        });

        return view;
    }

    private void getDir(String dirPath) {
        gallery_path.setText("Location: " + dirPath);
        file_item = new ArrayList<String>();
        file_path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(root)) {
            file_item.add(root);
            file_path.add(root);
            file_item.add("../");
            file_path.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (!file.isHidden() && file.canRead()) {
                file_path.add(file.getPath());
                if (file.isDirectory()) {
                    file_item.add(file.getName() + "/");
                } else {
                    file_item.add(file.getName());
                }
            }
        }

        gallery_files = file_item;

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(getActivity(), R.layout.row_item, file_item);
        gallery_listview.setAdapter(fileList);

        if(gallery_listview.getAdapter().getCount() != 0) {
            gallery_message.setVisibility(View.GONE);
            gallery_empty.setVisibility(View.GONE);
        }else {
            gallery_message.setVisibility(View.VISIBLE);
            gallery_empty.setVisibility(View.VISIBLE);
        }
    }


    public static void resetGallery(Context context){
        List<String> item = new ArrayList<String>();
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(context, R.layout.row_item, item);
        gallery_listview.setAdapter(fileList);
        gallery_empty.setVisibility(View.VISIBLE);
    }

}

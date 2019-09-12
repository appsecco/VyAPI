package com.appsecco.vyapi;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.appsecco.vyapi.contacts.ContactAdapter;
import com.appsecco.vyapi.contacts.ContactViewModel;
import com.appsecco.vyapi.fragments.CameraFragment;
import com.appsecco.vyapi.fragments.GalleryFragment;
import com.appsecco.vyapi.fragments.HomeFragment;
import com.appsecco.vyapi.fragments.PlayMusicFragment;
import com.appsecco.vyapi.fragments.ViewWebsiteFragment;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ContactViewModel contactViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Set Default Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        // Get an instance of ContactViewModel
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // Reference to Adapter
        final ContactAdapter adapter = new ContactAdapter();

        Toast.makeText(this, "Welcome " + AWSMobileClient.getInstance().getUsername() + "!", Toast.LENGTH_LONG).show();

        //Allowing Strict mode policy for Nougat support
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_delete_all_contacts:
                contactViewModel.deleteAllContacts();
                Toast.makeText(this, "All contacts deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_delete_all_images:
                List<String> files = GalleryFragment.gallery_files;
                String root = GalleryFragment.root;
                File fs;

                // Delete all files
                for (int i = 0; i < files.size(); i++) {
                    fs = new File(root + "/" + files.get(i));
                    fs.delete();
                }

                // Refresh list
                GalleryFragment.resetGallery(this);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (id == R.id.nav_play_music) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayMusicFragment()).commit();
        } else if(id == R.id.nav_camera){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CameraFragment()).commit();
        }else if(id == R.id.nav_gallery){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GalleryFragment()).commit();
        }else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_BACK:
                    WebView webView = ViewWebsiteFragment.wv_personal_website;
                    if(webView.canGoBack()){
                        webView.goBack();
                    } else{
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    // Sign out
    public void signOut(){
        AWSMobileClient.getInstance().signOut();
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        Toast.makeText(this, "Say Cheeese!!!", Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        if(CameraFragment.myBroadcastReceiver != null){
            try {
                unregisterReceiver(CameraFragment.myBroadcastReceiver);
            }catch (Exception e){
                // Do nothing
            }
        }
        super.onDestroy();
    }
}

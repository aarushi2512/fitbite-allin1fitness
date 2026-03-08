package com.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Fragment.BMIandBMR;
import com.example.Fragment.ChangePassFrag;
import com.example.Fragment.DietPlan;
import com.example.Fragment.ManageItemsFrag;
import com.example.Fragment.ProfileFrag;
import com.example.Fragment.SearchNutritionist;
import com.example.Fragment.ShoppingList;
import com.example.Fragment.SuggestRecipes;
import com.example.Utils.GPSTracker;
import com.example.Utils.GpsUtils;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    boolean isGps;
    ImageView hamburgerMenu;
    private FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle item clicks here
                int itemId = item.getItemId();
                if (itemId == R.id.nav_item) {
                    ManageItemsFrag mi = new ManageItemsFrag();
                    replace(mi);
                } else if (itemId == R.id.nav_suggestrecipes) {
                    SuggestRecipes sr = new SuggestRecipes();
                    replace(sr);
                } else if (itemId == R.id.nav_shoppinglist) {
                    ShoppingList sr = new ShoppingList();
                    replace(sr);
                } else if (itemId == R.id.nav_bmiandbmr) {
                    BMIandBMR bmIandBMR = new BMIandBMR();
                    replace(bmIandBMR);
                } else if (itemId == R.id.nav_dietplan) {
                    DietPlan dp = new DietPlan();
                    replace(dp);
                } else if (itemId == R.id.nav_nutritionist) {
                    if (isGPSEnabled()) {
                        SearchNutritionist searchNutritionist = new SearchNutritionist();
                        replace(searchNutritionist);
                    }
                } else if (itemId == R.id.nav_cp) {
                    ChangePassFrag cp = new ChangePassFrag();
                    replace(cp);
                } else if (itemId == R.id.nav_profile) {
                    ProfileFrag frag = new ProfileFrag();
                    replace(frag);
                } else if (itemId == R.id.nav_logout) {
                    Util.setSP(getApplicationContext(), "");
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }

                // Close the drawer
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ManageItemsFrag mi = new ManageItemsFrag();
        replace(mi);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the ActionBarDrawerToggle
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, fragment); // replace a Fragment with Frame Layout
        transaction.commit();

    }

    private boolean isGPSEnabled() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isProviderEnabled) {
                isGps = true;
            } else new GpsUtils(MainActivity.this).turnOnGps(isGPSEnable -> {
                isGps = isGPSEnable;

            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return isGps;
    }


}


package com.example.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


public class GPSTracker extends Service  {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 0;

    private static  Context context;
    private static onLatLongChangeListener onLatLongChangeListener;

    static boolean isGPSEnabled = false;
    static boolean isNetworkEnabled = false;
    static boolean canGetLocation = false;

    public static Location location;
    protected static LocationManager locationManager;

//    public GPSTracker(Context context) {
//        this.context = context;
//        getLocation();
//    }


    public static Location getLocation(Context context) {
        LocationListener locationListener = getListener();

        try {

            ((Activity) context).runOnUiThread(() -> {
                locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (isGPSEnabled || isNetworkEnabled) {
                    GPSTracker.canGetLocation = true;
                    if (isNetworkEnabled) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,locationListener
                                );
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            setLocation(location);
                        }
                    }
                    if (isGPSEnabled) {
                        if (location == null) {
                            if (locationManager != null) {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                setLocation(location);
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopUsingGPS(locationListener);
        return location;
    }

    @NonNull
    private static LocationListener getListener() {
        return location -> setLocation(location);
    }

    public static void stopUsingGPS(LocationListener locationListener) {
        try {
            if (locationManager != null) locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public static void setLocation(Location location) {
        if (location != null) GPSTracker.location = location;
        if (onLatLongChangeListener != null) onLatLongChangeListener.onChange();
    }

    public static void setLatLongChangeListener(onLatLongChangeListener onLatLongChangeListener) {
        GPSTracker.onLatLongChangeListener = onLatLongChangeListener;
    }

    public interface onLatLongChangeListener {
        void onChange();
    }
}
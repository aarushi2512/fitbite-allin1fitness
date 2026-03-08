package com.example.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;

public class GpsUtils {

    private final Context context;
    private final SettingsClient settingsClient;
    private final LocationManager locationManager;
    private final LocationSettingsRequest locationSettingsRequest;
    public static final int GPS_REQUEST_CODE = 1;

    public GpsUtils(Context context) {
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        settingsClient = LocationServices.getSettingsClient(context);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
        builder.setAlwaysShow(true);
    }

    public void turnOnGps(onGpsListener gpsListener) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsListener.gpsStatus(true);
        } else {
            settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(
                    task -> gpsListener.gpsStatus(true)
            ).addOnFailureListener(e -> {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult((Activity) context, GPS_REQUEST_CODE);
                        }
                        catch (Exception ignored) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        GpsUtils.buildAlertMessageNoGps((Activity) context);
                        Toast.makeText((Activity) context, "Turn on Gps from Settings", Toast.LENGTH_LONG).show();
                        break;
                }
            });
        }
    }

    public static void buildAlertMessageNoGps(Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false).setPositiveButton("Yes", (dialog, id) -> {
            dialog.cancel();
            activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }).setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static boolean checkForGoogleService(Context context, int errorCode) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog((Activity) context, result, errorCode);
            if (dialog != null) {
                dialog.show();
            }
        } else {
            Toast.makeText(context, "Play Service required", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public interface onGpsListener {
        void gpsStatus(boolean isGPSEnable);
    }
}


package com.example.Fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Utils.GPSTracker;
import com.example.Utils.GpsUtils;
import com.example.Utils.PermissionUtils;
import com.example.allin1fitnessuser.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchNutritionist extends Fragment implements OnMapReadyCallback {
    private static final int PLAY_SERVICE_ERROR_CODE = 8888;
    private GoogleMap mMap;

    Context context;

    LinearLayout coordinatorLay;
    Toolbar toolbar;

    //    private Location location = GPSTracker.location;
    private boolean isGps = false;
    private static final long UPDATE_INTERVAL = 10 * 1000;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 1;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.searchnutrionist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Nearby Nutritionist");
        coordinatorLay = view.findViewById(R.id.layout_Nutritionist);

        if (PermissionUtils.requestPermission(getActivity(), 101)) {
            isGPSEnabled();
        }

        if(isGPSEnabled()){
            initObj();
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                this.getChildFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationUpdates();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }


    private boolean isGPSEnabled() {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isProviderEnabled) {
                isGps = true;
            } else new GpsUtils(context).turnOnGps(isGPSEnable -> {
                isGps = isGPSEnable;

            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return isGps;
    }

    private void initObj() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                List<Location> locationList = locationResult.getLocations();
                for (Location location : locationList) {
                    Log.d("loc", String.valueOf(location.getLongitude()));
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("My Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
//                    Toast.makeText(context, String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
                    getPlace("Nutritionist", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                    break;
                }
            }


        };
        getLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void getLocationUpdates() {
        LocationRequest locationRequest =
                new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
                        .setWaitForAccurateLocation(false)
                        .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL)
                        .setMaxUpdateDelayMillis(MAX_WAIT_TIME)
                        .build();
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.myLooper());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void getPlace(String Category, String lat, String lng) {

        String URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + Category + "&location=" + lat + "," + lng + "&radius=10&key=AIzaSyAhywGgq9Wijf3LMZOLVDAV8lQ8Buf5OK8";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("resp", response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    ArrayList<Location> distanceList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.getJSONObject(i);
                        JSONObject geoObj = jobj.getJSONObject("geometry");
                        JSONObject locObj = geoObj.getJSONObject("location");
                        JSONObject name = geoObj.getJSONObject("location");
                        String lat = locObj.getString("lat");
                        String lon = locObj.getString("lng");
                        String sname = jobj.getString("name");
                        String srating = jobj.getString("rating");
                        String saddress = jobj.getString("formatted_address");

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)))
                                .title(sname));

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                // Create an alert builder
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("DETAILS");

                                // set the custom layout
                                final View customLayout = getLayoutInflater().inflate(R.layout.layout_detail, null);
                                TextView name = customLayout.findViewById(R.id.detail_name);
                                TextView rating = customLayout.findViewById(R.id.detail_rating);
                                TextView add = customLayout.findViewById(R.id.detail_add);

                                name.setText("Clinic Name: " + sname);
                                rating.setText("Rating: " + srating);
                                add.setText("Address: " + saddress);
                                builder.setView(customLayout);
                                AlertDialog dialog = builder.create();
                                dialog.show();

                                return false;
                            }
                        });

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            Log.d("error", error.getMessage());
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
}









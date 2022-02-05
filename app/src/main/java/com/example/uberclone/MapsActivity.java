package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;
    Marker marker;
    Button button;
    boolean uberCancel = false;

    //onClick send uber pickup request
    public void callUber(View view){

        if (uberCancel){
            ParseQuery<ParseObject> query = new ParseQuery<>("Request");
            query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() > 0){
                        for (ParseObject object : objects){
                            object.deleteInBackground();
                        }
                        uberCancel = false;
                        button.setText("Uber");
                    }
                }
            });
        }else{
            ParseObject request = new ParseObject("Request");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                //request location update ever sec/move using network or gps
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                //get users last location
                Location LastKnownLocation_gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location LastKnownLocation_network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (LastKnownLocation_gps != null) {
                    request.put("username", ParseUser.getCurrentUser().getUsername());
                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint(LastKnownLocation_gps.getLatitude(),LastKnownLocation_gps.getLongitude());
                    request.put("location", parseGeoPoint);
                } else if (LastKnownLocation_network != null) {
                    request.put("username", ParseUser.getCurrentUser().getUsername());
                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint(LastKnownLocation_network.getLatitude(),LastKnownLocation_network.getLongitude());
                    request.put("location", parseGeoPoint);
                } else {
                    //the users location is unknown
                    Toast.makeText(this, "Location uncertain", Toast.LENGTH_SHORT).show();
                }
            }

            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        button.setText("Cancel");
                        uberCancel = true;
                    }
                }
            });
        }
    }


    //center map on a location (likely the users) and places a marker
    public void centerMap(Location location){
        LatLng userlatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlatLng,18));
        marker = mMap.addMarker(new MarkerOptions().position(userlatLng).title("You"));
    }

    //Initialize user location and request location updates
    public void initialize(LocationManager locationManager, LocationListener locationListener){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            //request location update ever sec/move using network or gps
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            //when app initially launches we need to have a location
            Location LastKnownLocation_gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LastKnownLocation_network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (LastKnownLocation_gps != null){
                centerMap(LastKnownLocation_gps);
            } else if (LastKnownLocation_network != null){
                centerMap(LastKnownLocation_network);
            }else {
                //the users location is unknown
                Toast.makeText(this, "Location uncertain", Toast.LENGTH_SHORT).show();
                LatLng unknown = new LatLng(0,0);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(unknown));
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button = findViewById(R.id.button);

        ParseQuery<ParseObject> query = new ParseQuery<>("Request");
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0){
                    uberCancel = true;
                    button.setText("Cancel");
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager =  (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                marker.remove();
                centerMap(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        //check if we have the users permission [note: checking is only required for sdk>=23]
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //since we don't have the users permission we request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else {
            //the sdk<23 or we already have users permission
              initialize(locationManager,locationListener);

        }

    }

    //what to do once the user responds to permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                initialize(locationManager,locationListener);

            }
        }
    }
}
